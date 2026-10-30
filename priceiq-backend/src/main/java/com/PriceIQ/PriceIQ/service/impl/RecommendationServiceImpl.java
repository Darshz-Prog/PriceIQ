package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.response.ProductImageResponse;
import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.ProductAttribute;
import com.PriceIQ.PriceIQ.entity.ProductStatistic;
import com.PriceIQ.PriceIQ.entity.UserRecommendation;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.OrderItemRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.ProductStatisticRepository;
import com.PriceIQ.PriceIQ.repository.UserRecommendationRepository;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hybrid recommendation engine.
 *
 * ── Personalized feed  (/feed) ─────────────────────────────────────────────
 *   Reads pre-computed user_recommendations (written async by RecommendationConsumer).
 *   Cold-start fallback: trending products when no stored recs exist.
 *
 * ── Similar products  (/similar/{id}) ──────────────────────────────────────
 *   Content-based. Score = categoryMatch(1.0) + brandMatch(0.5)
 *                         + priceSimilarity(0..0.3) + attributeJaccard(0..0.2)
 *   Tie-break: productId ASC (deterministic).
 *   Filters: active=true, stockQuantity>0, id≠source.
 *   Fallback (no category-mates): highest-rated active in-stock products.
 *
 * ── Frequently Bought Together  (/fbt/{id}) ─────────────────────────────────
 *   Co-purchase signal: products that appear in the same orders.
 *   Fallback: same-category products ordered by purchaseCount desc.
 *   Filters: active=true, stockQuantity>0, id≠source.
 *   Does NOT recommend category-mates without actual co-purchase evidence
 *   (fallback is only triggered when zero co-purchase data exists).
 *
 * ── Trending  (/trending) ──────────────────────────────────────────────────
 *   trendScore = views×1 + carts×3 + purchases×10
 *   Filters: active=true, stockQuantity>0.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

    // ── Weights (content-based similarity) ──────────────────────────────────
    /** Exact brand name match bonus. */
    private static final double W_BRAND      = 0.50;
    /** Price similarity contribution — max when prices are identical. */
    private static final double W_PRICE      = 0.30;
    /** Attribute Jaccard overlap contribution. */
    private static final double W_ATTR       = 0.20;

    // ── Trend formula weights ────────────────────────────────────────────────
    private static final double W_VIEW       = 1.0;
    private static final double W_CART       = 3.0;
    private static final double W_PURCHASE   = 10.0;

    private final UserRecommendationRepository recommendationRepository;
    private final ProductRepository            productRepository;
    private final ProductStatisticRepository   productStatisticRepository;
    private final OrderItemRepository          orderItemRepository;

    // ════════════════════════════════════════════════════════════════════════
    // 1. Personalized feed
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public List<ProductResponse> getFeedForUser(Long userId) {
        List<UserRecommendation> recs =
                recommendationRepository.findByUserIdOrderByRankAsc(userId);

        if (!recs.isEmpty()) {
            return recs.stream()
                    .map(r -> mapToProductResponse(r.getProduct()))
                    .filter(this::isPurchasable)   // extra safety gate
                    .toList();
        }

        // Cold-start: fall back to trending
        log.debug("[recommendations] No stored recs for user={}, using trending fallback", userId);
        return getTrendingProducts(20);
    }

    // ════════════════════════════════════════════════════════════════════════
    // 2. Similar products (content-based)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public List<ProductResponse> getSimilarProducts(Long productId, int limit) {
        Product source = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        // Candidates: same category, active, in-stock, excluding source
        List<Product> candidates = productRepository
                .findByCategoryAndActiveTrueAndStockQuantityGreaterThan(
                        source.getCategory(), 0);

        candidates = candidates.stream()
                .filter(p -> !p.getId().equals(productId))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            // Fallback: any active in-stock products sorted by rating, excluding source
            log.debug("[recommendations] No same-category candidates for product={}, using rating fallback", productId);
            return productRepository.findByActiveTrue().stream()
                    .filter(p -> !p.getId().equals(productId)
                            && p.getStockQuantity() != null && p.getStockQuantity() > 0)
                    .sorted(Comparator
                            .comparingDouble((Product p) -> p.getAverageRating() == null ? 0.0 : p.getAverageRating())
                            .reversed()
                            .thenComparingLong(Product::getId))
                    .limit(limit)
                    .map(this::mapToProductResponse)
                    .toList();
        }

        Set<String> srcAttrKeys = attributeKeys(source);

        return candidates.stream()
                .map(c -> Map.entry(c, contentScore(source, c, srcAttrKeys)))
                .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder())
                        .thenComparingLong(e -> e.getKey().getId()))
                .limit(limit)
                .map(e -> mapToProductResponse(e.getKey()))
                .toList();
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. Frequently bought together (co-purchase signal)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public List<ProductResponse> getFrequentlyBoughtTogether(Long productId, int limit) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }

        // Co-purchase from order_items
        List<Long> coIds = orderItemRepository.findCoPurchasedProductIds(productId, limit);

        if (!coIds.isEmpty()) {
            // Fetch in one query, preserve co-purchase ranking order
            Map<Long, Product> byId = productRepository.findAllById(coIds)
                    .stream().collect(Collectors.toMap(Product::getId, p -> p));

            List<ProductResponse> results = coIds.stream()
                    .map(byId::get)
                    .filter(Objects::nonNull)
                    .filter(p -> Boolean.TRUE.equals(p.getActive()) || p.getActive() == null)
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                    .map(this::mapToProductResponse)
                    .toList();

            if (!results.isEmpty()) return results;
        }

        // Fallback: same category, sorted by purchase count — only when zero co-purchase evidence
        log.debug("[recommendations] No co-purchase data for product={}, using category+popularity fallback", productId);
        Product source = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        // Build a popularity map from product_statistics
        Map<Long, Long> purchaseCounts = productStatisticRepository.findAll()
                .stream()
                .collect(Collectors.toMap(ProductStatistic::getProductId, ProductStatistic::getPurchaseCount));

        return productRepository.findByCategoryAndActiveTrueAndStockQuantityGreaterThan(
                        source.getCategory(), 0).stream()
                .filter(p -> !p.getId().equals(productId))
                .sorted(Comparator
                        .comparingLong((Product p) -> purchaseCounts.getOrDefault(p.getId(), 0L))
                        .reversed()
                        .thenComparingLong(Product::getId))
                .limit(limit)
                .map(this::mapToProductResponse)
                .toList();
    }

    // ════════════════════════════════════════════════════════════════════════
    // 4. Trending products
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public List<ProductResponse> getTrendingProducts(int limit) {
        // Load all stats and sort by trendScore in memory (table is small — one row per product)
        List<ProductStatistic> stats = productStatisticRepository.findAll();

        if (stats.isEmpty()) {
            // Absolute fallback: newest active in-stock products
            return productRepository.findByActiveTrue().stream()
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                    .sorted(Comparator.comparingLong(Product::getId).reversed())
                    .limit(limit)
                    .map(this::mapToProductResponse)
                    .toList();
        }

        // Sort stats by trend score desc, then productId asc for determinism
        List<Long> trendingIds = stats.stream()
                .sorted(Comparator
                        .comparingDouble((ProductStatistic s) -> trendScore(s))
                        .reversed()
                        .thenComparingLong(ProductStatistic::getProductId))
                .map(ProductStatistic::getProductId)
                .collect(Collectors.toList());

        // Fetch products in one query, then filter and preserve order
        Map<Long, Product> byId = productRepository.findAllById(trendingIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        return trendingIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .filter(this::isPurchasableProduct)
                .limit(limit)
                .map(this::mapToProductResponse)
                .toList();
    }

    // ════════════════════════════════════════════════════════════════════════
    // Scoring helpers
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Content-based similarity score for a candidate against the source product.
     * Category match is implicit (candidates are already filtered by category).
     * Score range: 0.0 … 1.0 (higher = more similar)
     */
    private double contentScore(Product source, Product candidate, Set<String> srcAttrKeys) {
        double score = 0.0;

        // Brand match
        if (source.getBrand() != null && source.getBrand().equalsIgnoreCase(candidate.getBrand())) {
            score += W_BRAND;
        }

        // Relative price similarity: 1 - |delta| / sourcePrice, capped at [0,1]
        if (source.getCurrentPrice() != null && candidate.getCurrentPrice() != null
                && source.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0) {
            double delta = source.getCurrentPrice().subtract(candidate.getCurrentPrice())
                    .abs().doubleValue();
            double priceSim = Math.max(0.0, 1.0 - delta / source.getCurrentPrice().doubleValue());
            score += priceSim * W_PRICE;
        }

        // Attribute Jaccard overlap
        if (!srcAttrKeys.isEmpty()) {
            Set<String> candKeys = attributeKeys(candidate);
            if (!candKeys.isEmpty()) {
                Set<String> intersection = new HashSet<>(srcAttrKeys);
                intersection.retainAll(candKeys);
                Set<String> union = new HashSet<>(srcAttrKeys);
                union.addAll(candKeys);
                score += ((double) intersection.size() / union.size()) * W_ATTR;
            }
        }

        return score;
    }

    /** Trend score formula: views×1 + carts×3 + purchases×10 */
    private double trendScore(ProductStatistic s) {
        return s.getViewCount() * W_VIEW
                + s.getCartCount() * W_CART
                + s.getPurchaseCount() * W_PURCHASE;
    }

    /** Returns the set of attribute names for a product (empty if none). */
    private Set<String> attributeKeys(Product p) {
        if (p.getAttributes() == null || p.getAttributes().isEmpty()) return Collections.emptySet();
        return p.getAttributes().stream()
                .map(ProductAttribute::getAttributeName)
                .collect(Collectors.toSet());
    }

    /** True if a product entity can be purchased: active (or null treated as true) and in stock. */
    private boolean isPurchasableProduct(Product p) {
        return !Boolean.FALSE.equals(p.getActive())
                && p.getStockQuantity() != null && p.getStockQuantity() > 0;
    }

    /** True if a product DTO can be purchased (used for post-mapping safety gate). */
    private boolean isPurchasable(ProductResponse r) {
        return !Boolean.FALSE.equals(r.getActive())
                && r.getStockQuantity() != null && r.getStockQuantity() > 0;
    }

    // ════════════════════════════════════════════════════════════════════════
    // Mapping — mirrors ProductServiceImpl.mapToResponse
    // ════════════════════════════════════════════════════════════════════════

    private ProductResponse mapToProductResponse(Product product) {
        List<ProductImageResponse> images = product.getImages() == null
                ? List.of()
                : product.getImages().stream()
                        .map(img -> ProductImageResponse.builder()
                                .id(img.getId())
                                .imageUrl(img.getImageUrl())
                                .primaryImage(img.getPrimaryImage())
                                .displayOrder(img.getDisplayOrder())
                                .altText(img.getAltText())
                                .build())
                        .toList();

        Map<String, String> attrMap = (product.getAttributes() == null || product.getAttributes().isEmpty())
                ? null
                : product.getAttributes().stream()
                        .collect(Collectors.toMap(
                                ProductAttribute::getAttributeName,
                                ProductAttribute::getAttributeValue,
                                (a, b) -> b));

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .brand(product.getBrand())
                .category(product.getCategory())
                .basePrice(product.getBasePrice())
                .currentPrice(product.getCurrentPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive() != null ? product.getActive() : Boolean.TRUE)
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .images(images)
                .attributes(attrMap)
                .build();
    }
}
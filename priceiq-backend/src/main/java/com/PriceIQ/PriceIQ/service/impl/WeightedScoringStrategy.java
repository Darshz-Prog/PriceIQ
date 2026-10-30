package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.entity.ProductStatistic;
import com.PriceIQ.PriceIQ.entity.UserProductFeature;
import com.PriceIQ.PriceIQ.repository.ProductStatisticRepository;
import com.PriceIQ.PriceIQ.service.ScoringStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Weighted scoring strategy for personalized recommendations.
 *
 * Formula:
 *   personalizedScore = (views × VIEW_W) + (carts × CART_W)
 *                     + (purchases × PURCHASE_W) + (wishlists × WISHLIST_W)
 *                     + recencyBonus
 *                     + normalizedPopularity × POPULARITY_W
 *
 * Weights (documented assumptions):
 *   - VIEW     1.0  — lightweight signal; easy to accumulate
 *   - WISHLIST 2.0  — explicit intent, stronger than a view
 *   - CART     3.0  — strong purchase intent
 *   - PURCHASE 10.0 — strongest positive signal
 *   - POPULARITY 0.5 — global trend nudges cold-start and boosts trending items
 *
 * Recency bonus (logarithmic decay):
 *   - Same day   → +5
 *   - This week  → +3
 *   - This month → +1
 *   - Older      →  0
 *
 * Popularity normalization:
 *   trendScore(p) = viewCount×1 + cartCount×3 + purchaseCount×10
 *   normalizedPopularity = trendScore(p) / maxTrendScore (across all products)
 *   This keeps the popularity contribution bounded to [0, POPULARITY_W].
 *
 * To replace with an ML model: implement ScoringStrategy in a new @Primary class.
 */
@Service
@RequiredArgsConstructor
public class WeightedScoringStrategy implements ScoringStrategy {

    // ── Personalization weights ──────────────────────────────────────────────
    private static final double VIEW_W       = 1.0;
    private static final double WISHLIST_W   = 2.0;
    private static final double CART_W       = 3.0;
    private static final double PURCHASE_W   = 10.0;
    private static final double POPULARITY_W = 0.5;

    private final ProductStatisticRepository productStatisticRepository;

    /**
     * Simple in-process cache for the max trend score.
     * Recomputed at most once per JVM minute to avoid hammering the DB.
     */
    private volatile double cachedMaxTrend   = 1.0;
    private volatile long   cacheRefreshedAt = 0L;
    private static final long CACHE_TTL_MS   = 60_000L;

    @Override
    public double score(UserProductFeature feature) {
        double base = (feature.getViewCount()     * VIEW_W)
                    + (feature.getCartCount()     * CART_W)
                    + (feature.getPurchaseCount() * PURCHASE_W)
                    + (feature.getWishlistCount() * WISHLIST_W);

        double popularity = normalizedPopularity(feature.getProduct().getId());

        return base + recencyBonus(feature.getLastInteraction()) + popularity * POPULARITY_W;
    }

    // ── Recency ──────────────────────────────────────────────────────────────

    private double recencyBonus(LocalDateTime lastInteraction) {
        if (lastInteraction == null) return 0.0;
        long daysSince = Duration.between(lastInteraction, LocalDateTime.now()).toDays();
        if (daysSince == 0)  return 5.0;
        if (daysSince <= 7)  return 3.0;
        if (daysSince <= 30) return 1.0;
        return 0.0;
    }

    // ── Popularity normalization ──────────────────────────────────────────────

    private double normalizedPopularity(Long productId) {
        refreshCacheIfStale();
        return productStatisticRepository.findById(productId)
                .map(s -> trendScore(s) / Math.max(1.0, cachedMaxTrend))
                .orElse(0.0);
    }

    private double trendScore(ProductStatistic s) {
        return s.getViewCount()     * VIEW_W
             + s.getCartCount()     * CART_W
             + s.getPurchaseCount() * PURCHASE_W;
    }

    private void refreshCacheIfStale() {
        long now = System.currentTimeMillis();
        if (now - cacheRefreshedAt > CACHE_TTL_MS) {
            double max = productStatisticRepository.findAll().stream()
                    .mapToDouble(this::trendScore)
                    .max()
                    .orElse(1.0);
            cachedMaxTrend   = Math.max(max, 1.0);
            cacheRefreshedAt = now;
        }
    }
}

package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.response.ProductImageResponse;
import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.UserRecommendation;
import com.PriceIQ.PriceIQ.repository.UserRecommendationRepository;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reads pre-computed recommendations from user_recommendations.
 * No Kafka, no scoring — that all happens asynchronously in RecommendationConsumer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserRecommendationRepository recommendationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeedForUser(Long userId) {
        List<UserRecommendation> recs =
                recommendationRepository.findByUserIdOrderByRankAsc(userId);

        if (recs.isEmpty()) {
            log.debug("No recommendations stored yet for user={}", userId);
            return List.of();
        }

        return recs.stream()
                .map(rec -> mapToProductResponse(rec.getProduct()))
                .toList();
    }

    // ── Mapping (mirrors ProductServiceImpl.mapToResponse) ───────────────────

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

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .basePrice(product.getBasePrice())
                .currentPrice(product.getCurrentPrice())
                .stockQuantity(product.getStockQuantity())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .images(images)
                .build();
    }
}
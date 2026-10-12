package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.entity.*;
import com.PriceIQ.PriceIQ.repository.*;
import com.PriceIQ.PriceIQ.service.ScoringStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer group: recommendation-service
 *
 * Responsibility:
 *   1. UPSERT user_product_features based on the event type.
 *   2. After each update, recompute and store top-N recommendations
 *      for the user into user_recommendations.
 *
 * The /feed endpoint reads from user_recommendations without touching Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationConsumer {

    private static final String GROUP_ID   = "recommendation-service";
    private static final int    TOP_N      = 20;

    private final UserProductFeatureRepository featureRepository;
    private final UserRecommendationRepository recommendationRepository;
    private final ProductRepository            productRepository;
    private final UserRepository               userRepository;
    private final ScoringStrategy              scoringStrategy;
    private final ObjectMapper                 objectMapper;

    @KafkaListener(topics = "user-events", groupId = GROUP_ID)
    @Transactional
    public void consume(String message) {
        try {
            EventRequest event = objectMapper.readValue(message, EventRequest.class);

            if (event.getUserId() == null || event.getProductId() == null) {
                return; // Events like SEARCH have no productId — skip feature update
            }

            upsertFeature(event);
            regenerateRecommendations(event.getUserId());

        } catch (Exception e) {
            log.error("[recommendation-service] Failed to process message: {} — {}", message, e.getMessage());
        }
    }

    // ── Feature UPSERT ────────────────────────────────────────────────────────

    private void upsertFeature(EventRequest event) {
        UserProductFeatureId featureId =
                new UserProductFeatureId(event.getUserId(), event.getProductId());

        UserProductFeature feature = featureRepository.findById(featureId)
                .orElseGet(() -> buildNewFeature(event.getUserId(), event.getProductId()));

        switch (event.getEventType()) {
            case PRODUCT_VIEW, PRODUCT_CLICK -> feature.setViewCount(feature.getViewCount() + 1);
            case ADD_TO_CART                 -> feature.setCartCount(feature.getCartCount() + 1);
            case REMOVE_FROM_CART            -> feature.setCartCount(Math.max(0, feature.getCartCount() - 1));
            case PURCHASE                    -> feature.setPurchaseCount(feature.getPurchaseCount() + 1);
            case ADD_TO_WISHLIST             -> feature.setWishlistCount(feature.getWishlistCount() + 1);
            case REMOVE_FROM_WISHLIST        -> feature.setWishlistCount(Math.max(0, feature.getWishlistCount() - 1));
            default -> { /* SEARCH, LOGIN, REVIEW — no feature update */ }
        }
        feature.setLastInteraction(LocalDateTime.now());
        featureRepository.save(feature);

        log.debug("[recommendation-service] Upserted feature for user={} product={}",
                event.getUserId(), event.getProductId());
    }

    private UserProductFeature buildNewFeature(Long userId, Long productId) {
        User user       = userRepository.getReferenceById(userId);
        Product product = productRepository.getReferenceById(productId);
        return UserProductFeature.builder()
                .id(new UserProductFeatureId(userId, productId))
                .user(user)
                .product(product)
                .lastInteraction(LocalDateTime.now())
                .build();
    }

    // ── Recommendation generation ─────────────────────────────────────────────

    private void regenerateRecommendations(Long userId) {
        List<UserProductFeature> features =
                featureRepository.findByUserIdOrderByViewCountDesc(userId);

        if (features.isEmpty()) {
            return;
        }

        // Score all features, sort descending, take top N
        List<UserProductFeature> topFeatures = features.stream()
                .sorted(Comparator.comparingDouble(scoringStrategy::score).reversed())
                .limit(TOP_N)
                .toList();

        User userRef = userRepository.getReferenceById(userId);

        // Atomically replace old recs
        recommendationRepository.deleteByUserId(userId);

        AtomicInteger rank = new AtomicInteger(1);
        List<UserRecommendation> recs = topFeatures.stream()
                .map(f -> UserRecommendation.builder()
                        .user(userRef)
                        .product(f.getProduct())
                        .score(scoringStrategy.score(f))
                        .rank(rank.getAndIncrement())
                        .build())
                .toList();

        recommendationRepository.saveAll(recs);

        log.debug("[recommendation-service] Stored {} recommendations for user={}",
                recs.size(), userId);
    }
}

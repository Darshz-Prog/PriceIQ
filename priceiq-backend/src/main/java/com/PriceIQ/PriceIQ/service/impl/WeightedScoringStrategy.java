package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.entity.UserProductFeature;
import com.PriceIQ.PriceIQ.service.ScoringStrategy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Default math-based scoring strategy.
 *
 * Formula:
 *   score = (views × 1.0) + (carts × 3.0) + (purchases × 10.0) + (wishlists × 2.0)
 *           + recency_bonus
 *
 * Recency bonus: decays logarithmically.
 *   - If interacted today     → +5
 *   - If interacted this week → +3
 *   - If interacted this month→ +1
 *   - Older                   → 0
 *
 * To replace this with an ML model, implement ScoringStrategy in a new class
 * and annotate it with @Primary.
 */
@Service
public class WeightedScoringStrategy implements ScoringStrategy {

    private static final double WEIGHT_VIEW     = 1.0;
    private static final double WEIGHT_CART     = 3.0;
    private static final double WEIGHT_PURCHASE = 10.0;
    private static final double WEIGHT_WISHLIST = 2.0;

    @Override
    public double score(UserProductFeature feature) {
        double base = (feature.getViewCount()     * WEIGHT_VIEW)
                    + (feature.getCartCount()     * WEIGHT_CART)
                    + (feature.getPurchaseCount() * WEIGHT_PURCHASE)
                    + (feature.getWishlistCount() * WEIGHT_WISHLIST);

        return base + recencyBonus(feature.getLastInteraction());
    }

    private double recencyBonus(LocalDateTime lastInteraction) {
        if (lastInteraction == null) {
            return 0.0;
        }
        long daysSince = Duration.between(lastInteraction, LocalDateTime.now()).toDays();
        if (daysSince == 0) return 5.0;
        if (daysSince <= 7) return 3.0;
        if (daysSince <= 30) return 1.0;
        return 0.0;
    }
}

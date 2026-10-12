package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.entity.UserProductFeature;

/**
 * Pluggable scoring strategy for recommendation ranking.
 *
 * Replace the default implementation with any ML model by implementing this
 * interface and annotating it with @Primary (or via @ConditionalOn...).
 */
public interface ScoringStrategy {

    /**
     * Computes a relevance score for a given user-product feature row.
     * Higher score = more relevant / recommended.
     */
    double score(UserProductFeature feature);
}

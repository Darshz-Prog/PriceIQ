package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;

import java.util.List;

public interface RecommendationService {

    /**
     * Returns the pre-computed personalized product feed for a user.
     * Does NOT run scoring or touch Kafka — reads from user_recommendations table only.
     */
    List<ProductResponse> getFeedForUser(Long userId);
}
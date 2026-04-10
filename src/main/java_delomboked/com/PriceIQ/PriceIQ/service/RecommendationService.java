package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.response.RecommendationResponse;

public interface RecommendationService {

    RecommendationResponse getRecommendationsForUser(Long userId);

    void reloadModel();
}
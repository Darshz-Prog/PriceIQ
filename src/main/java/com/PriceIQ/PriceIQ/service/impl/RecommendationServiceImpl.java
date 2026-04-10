package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.MlRecommendationRequest;
import com.PriceIQ.PriceIQ.dto.response.MlRecommendationResponse;
import com.PriceIQ.PriceIQ.dto.response.RecommendationResponse;
import com.PriceIQ.PriceIQ.service.MlServiceClient;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import com.PriceIQ.PriceIQ.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final MlServiceClient mlServiceClient;
    private final RedisCacheService redisCacheService;

    @Override
    public RecommendationResponse getRecommendationsForUser(Long userId) {

        String cacheKey = "recommendation:user:" + userId;

        if (redisCacheService.exists(cacheKey)) {
            return (RecommendationResponse) redisCacheService.get(cacheKey);
        }
        MlRecommendationRequest request = new MlRecommendationRequest();

        MlRecommendationResponse mlResponse =
                mlServiceClient.getRecommendations(request);

        RecommendationResponse response = RecommendationResponse.builder()
                .userId(userId)
                .recommendedProducts(java.util.Collections.emptyList())
                .build();

        redisCacheService.save(cacheKey, response, Duration.ofMinutes(30));

        return response;
    }

    @Override
    public void reloadModel() {
        System.out.println("Reload recommendation model endpoint triggered");
    }
}
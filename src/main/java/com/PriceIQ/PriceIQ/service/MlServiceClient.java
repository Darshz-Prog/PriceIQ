package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.MlRecommendationRequest;
import com.PriceIQ.PriceIQ.dto.response.MlRecommendationResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MlServiceClient {
//! NOT COMPLETED
    private final RestTemplate restTemplate;

    public MlRecommendationResponse getRecommendations(
            MlRecommendationRequest request
    ) {

        String url = "http://localhost:8001/api/v1/recommendations";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MlRecommendationRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<MlRecommendationResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        MlRecommendationResponse.class
                );

        return response.getBody();
    }
}
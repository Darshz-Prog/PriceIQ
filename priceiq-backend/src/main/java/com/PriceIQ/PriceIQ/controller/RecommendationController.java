package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import com.PriceIQ.PriceIQ.util.UserContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserContextHelper userContextHelper;

    /**
     * Returns the personalized product feed for the authenticated user.
     * Reads from pre-computed user_recommendations — zero Kafka or scoring overhead.
     */
    @GetMapping("/feed")
    public ResponseEntity<List<ProductResponse>> getFeed() {
        Long userId = userContextHelper.getCurrentUserId()
                .orElseThrow(() -> new com.PriceIQ.PriceIQ.exception.ResourceNotFoundException(
                        "Authenticated user not found"));
        return ResponseEntity.ok(recommendationService.getFeedForUser(userId));
    }
}
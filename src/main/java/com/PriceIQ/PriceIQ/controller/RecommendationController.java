package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.response.RecommendationResponse;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService; //! NOT COMPLETED

    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                recommendationService.getRecommendationsForUser(userId)
        );
    }
}
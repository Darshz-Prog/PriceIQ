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
    private final UserContextHelper     userContextHelper;

    /**
     * GET /api/v1/recommendations/feed
     * Returns the personalized product feed for the authenticated user.
     * Reads from pre-computed user_recommendations; cold-start falls back to trending.
     * Auth: REQUIRED.
     */
    @GetMapping("/feed")
    public ResponseEntity<List<ProductResponse>> getFeed() {
        Long userId = userContextHelper.getCurrentUserId()
                .orElseThrow(() -> new com.PriceIQ.PriceIQ.exception.ResourceNotFoundException(
                        "Authenticated user not found"));
        return ResponseEntity.ok(recommendationService.getFeedForUser(userId));
    }

    /**
     * GET /api/v1/recommendations/similar/{productId}?limit=8
     * Content-based similar products for the given product.
     * Auth: PUBLIC (product-level, not user-specific).
     */
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<ProductResponse>> getSimilar(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "8") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return ResponseEntity.ok(recommendationService.getSimilarProducts(productId, safeLimit));
    }

    /**
     * GET /api/v1/recommendations/fbt/{productId}?limit=6
     * Frequently bought together for the given product.
     * Signal: co-purchase from order_items; falls back to category+popularity.
     * Auth: PUBLIC.
     */
    @GetMapping("/fbt/{productId}")
    public ResponseEntity<List<ProductResponse>> getFrequentlyBoughtTogether(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return ResponseEntity.ok(recommendationService.getFrequentlyBoughtTogether(productId, safeLimit));
    }

    /**
     * GET /api/v1/recommendations/trending?limit=12
     * Global trending products (views×1 + carts×3 + purchases×10).
     * Auth: PUBLIC.
     */
    @GetMapping("/trending")
    public ResponseEntity<List<ProductResponse>> getTrending(
            @RequestParam(defaultValue = "12") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return ResponseEntity.ok(recommendationService.getTrendingProducts(safeLimit));
    }
}
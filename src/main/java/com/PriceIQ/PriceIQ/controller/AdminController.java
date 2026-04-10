package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.PricingWeightsRequest;
import com.PriceIQ.PriceIQ.dto.response.GenericSuccessResponse;
import com.PriceIQ.PriceIQ.service.AnalyticsService;
import com.PriceIQ.PriceIQ.service.PricingAlgorithmService;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AnalyticsService analyticsService;
    private final PricingAlgorithmService pricingAlgorithmService;
    private final RecommendationService recommendationService;

    @PutMapping("/pricing-weights")
    public ResponseEntity<GenericSuccessResponse> updatePricingWeights(
            @Valid @RequestBody PricingWeightsRequest request
    ) {
        pricingAlgorithmService.updatePricingWeights(request);

        return ResponseEntity.ok(
                new GenericSuccessResponse(
                        true,
                        "Pricing weights updated successfully"
                )
        );
    }

    @PostMapping("/recommendation-model/reload")
    public ResponseEntity<GenericSuccessResponse> reloadRecommendationModel() {
        recommendationService.reloadModel();

        return ResponseEntity.ok(
                new GenericSuccessResponse(
                        true,
                        "Recommendation model reloaded successfully"
                )
        );
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> getAdminMetrics() {
        return ResponseEntity.ok(analyticsService.getAdminMetrics());
    }
}
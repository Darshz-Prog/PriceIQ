package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.response.AnalyticsResponse;
import com.PriceIQ.PriceIQ.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsResponse> getDashboardAnalytics() {
        return ResponseEntity.ok(analyticsService.getDashboardAnalytics());
    }

    @GetMapping("/trending-products")
    public ResponseEntity<?> getTrendingProducts() {
        return ResponseEntity.ok(analyticsService.getTrendingProducts());
    }

    @GetMapping("/conversion-rate")
    public ResponseEntity<?> getConversionRate() {
        return ResponseEntity.ok(analyticsService.getConversionRate());
    }
}
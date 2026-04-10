package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.service.PricingAlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PricingAlgorithmService pricingAlgorithmService;//! NOT COMPLETED

    @GetMapping("/{productId}")
    public ResponseEntity<Double> getLivePrice(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(
                pricingAlgorithmService.getDynamicPrice(productId, userId)
        );
    }
}
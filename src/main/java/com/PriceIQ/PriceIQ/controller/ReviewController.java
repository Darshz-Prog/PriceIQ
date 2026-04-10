package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.ReviewRequest;
import com.PriceIQ.PriceIQ.dto.response.ReviewResponse;
import com.PriceIQ.PriceIQ.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }
}
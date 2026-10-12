package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.ReviewRequest;
import com.PriceIQ.PriceIQ.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByProductId(Long productId);
}
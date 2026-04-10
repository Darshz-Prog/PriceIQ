package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationResponse {

    private Long userId;

    private String recommendationType;

    private List<ProductResponse> recommendedProducts;
}
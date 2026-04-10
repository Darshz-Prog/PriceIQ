package com.PriceIQ.PriceIQ.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MlRecommendationResponse {

    private List<Long> recommendedProductIds;
}
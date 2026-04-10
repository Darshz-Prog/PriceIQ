package com.PriceIQ.PriceIQ.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class MlRecommendationRequest {

    private Long userId;

    private List<Long> viewedProductIds;

    private List<Long> purchasedProductIds;

    private List<Long> cartProductIds;

    private Integer limit;
}
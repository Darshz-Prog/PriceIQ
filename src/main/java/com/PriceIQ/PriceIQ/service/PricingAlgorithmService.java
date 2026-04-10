package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.PricingWeightsRequest;

public interface PricingAlgorithmService {

    Double getDynamicPrice(Long productId, Long userId);

    void updatePricingWeights(PricingWeightsRequest request);
}
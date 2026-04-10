package com.PriceIQ.PriceIQ.dto.request;

import lombok.Data;

@Data
public class PricingWeightsRequest {

    private Double demandWeight;

    private Double stockWeight;

    private Double competitorWeight;

    private Double loyaltyWeight;

    private Double seasonalWeight;
}
package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsResponse {

    private Long totalUsers;

    private Long totalOrders;

    private Long totalProducts;

    private Long totalEvents;

    private BigDecimal totalRevenue;

    private Long totalReviews;

    private List<String> trendingProducts;

    private Map<String, Long> ordersByStatus;

    private Map<String, Long> eventsByType;

    private Map<String, Double> conversionRates;
}
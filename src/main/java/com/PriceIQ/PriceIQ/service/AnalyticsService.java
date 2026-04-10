package com.PriceIQ.PriceIQ.service;

import java.util.Map;

import com.PriceIQ.PriceIQ.dto.response.AnalyticsResponse;

public interface AnalyticsService {

    AnalyticsResponse getDashboardAnalytics();

    Object getTrendingProducts();

    Double getConversionRate();

    Map<String, Object> getAdminMetrics();
}
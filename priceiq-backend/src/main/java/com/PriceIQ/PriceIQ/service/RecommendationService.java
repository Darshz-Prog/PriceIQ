package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;

import java.util.List;

public interface RecommendationService {

    /**
     * Pre-computed personalized feed for a user.
     * Reads from user_recommendations (written async by Kafka consumer).
     * Falls back to trending products for new/cold-start users.
     */
    List<ProductResponse> getFeedForUser(Long userId);

    /**
     * Content-based similar products for a given product.
     * Ranked by: category match → brand match → relative price similarity → attribute overlap.
     * Excludes the source product, inactive products, and out-of-stock products.
     */
    List<ProductResponse> getSimilarProducts(Long productId, int limit);

    /**
     * Frequently-bought-together products for a given product.
     * Signal: products that appear in the same completed orders as this product.
     * Falls back to same-category products sorted by purchase count if no co-purchase data.
     * Excludes the source product, inactive products, and out-of-stock products.
     */
    List<ProductResponse> getFrequentlyBoughtTogether(Long productId, int limit);

    /**
     * Global trending products based on product_statistics.
     * Ranked by: (views * 1) + (carts * 3) + (purchases * 10), capped at limit.
     * Excludes inactive and out-of-stock products.
     */
    List<ProductResponse> getTrendingProducts(int limit);
}
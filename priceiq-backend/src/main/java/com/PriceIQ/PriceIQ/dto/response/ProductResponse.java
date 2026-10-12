package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private String sku;

    private String brand;

    private String category;

    private BigDecimal basePrice;

    private BigDecimal currentPrice;

    private Integer stockQuantity;

    private Double averageRating;

    private Integer totalReviews;

    private Boolean active;

    private List<ProductImageResponse> images;

    private Map<String, String> attributes;
}
package com.PriceIQ.PriceIQ.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductRequest {

    private String name;

    private String description;

    private String sku;

    private String brand;

    private String category;

    private BigDecimal basePrice;

    private BigDecimal currentPrice;

    private Integer stockQuantity;

    private Boolean active;

    private List<ProductImageRequest> images;

    private Map<String, String> attributes;
}
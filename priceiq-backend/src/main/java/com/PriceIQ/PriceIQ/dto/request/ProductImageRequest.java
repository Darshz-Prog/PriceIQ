package com.PriceIQ.PriceIQ.dto.request;

import lombok.Data;

@Data
public class ProductImageRequest {

    private String imageUrl;

    private Boolean primaryImage;

    private Integer displayOrder;

    private String altText;
}
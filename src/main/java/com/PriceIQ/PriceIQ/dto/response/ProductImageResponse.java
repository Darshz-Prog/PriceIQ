package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponse {

    private Long id;

    private String imageUrl;

    private Boolean primaryImage;

    private Integer displayOrder;

    private String altText;
}
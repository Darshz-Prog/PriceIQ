package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistResponse {

    private Long wishlistId;

    private Long userId;

    private Long productId;

    private String productName;

    private String productImage;

    private String brand;

    private String category;
}
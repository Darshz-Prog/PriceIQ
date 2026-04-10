package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String productImage;

    private Integer quantity;

    private BigDecimal priceAtAddition;

    private BigDecimal totalPrice;
}
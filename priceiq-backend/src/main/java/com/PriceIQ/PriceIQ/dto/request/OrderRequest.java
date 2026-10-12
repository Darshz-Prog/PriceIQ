package com.PriceIQ.PriceIQ.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotNull
    private Long userId;

    @NotNull
    private List<OrderItemRequest> items;

    @NotBlank
    private String shippingAddress;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String postalCode;

    private String couponCode;
}
package com.PriceIQ.PriceIQ.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WishlistRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long productId;
}
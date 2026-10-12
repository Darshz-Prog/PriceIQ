package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.CartRequest;
import com.PriceIQ.PriceIQ.dto.response.CartResponse;

public interface CartService {

    CartResponse getCartByUserId(Long userId);

    CartResponse addToCart(CartRequest request);

    CartResponse removeFromCart(Long cartItemId);

    CartResponse updateQuantity(Long cartItemId, Integer quantity);

    void clearCart(Long userId);
}
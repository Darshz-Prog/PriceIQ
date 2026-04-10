package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.WishlistRequest;
import com.PriceIQ.PriceIQ.dto.response.WishlistResponse;

public interface WishlistService {

    WishlistResponse getWishlistByUserId(Long userId);

    WishlistResponse addToWishlist(WishlistRequest request);

    WishlistResponse removeFromWishlist(Long userId, Long productId);
}
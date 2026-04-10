package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.WishlistRequest;
import com.PriceIQ.PriceIQ.dto.response.WishlistResponse;
import com.PriceIQ.PriceIQ.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistResponse>> getWishlist(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @Valid @RequestBody WishlistRequest request
    ) {
        return ResponseEntity.ok(wishlistService.addToWishlist(request));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<WishlistResponse> removeFromWishlist(
            @RequestParam Long userId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                wishlistService.removeFromWishlist(userId, productId)
        );
    }
}
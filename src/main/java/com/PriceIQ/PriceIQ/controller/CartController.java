package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.CartRequest;
import com.PriceIQ.PriceIQ.dto.response.CartResponse;
import com.PriceIQ.PriceIQ.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody CartRequest request
    ) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long cartItemId
    ) {
        return ResponseEntity.ok(cartService.removeFromCart(cartItemId));
    }

    @PutMapping("/update-quantity/{cartItemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(cartService.updateQuantity(cartItemId, quantity));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long userId
    ) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
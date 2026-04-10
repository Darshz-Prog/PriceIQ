package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.OrderRequest;
import com.PriceIQ.PriceIQ.dto.response.OrderResponse;
import com.PriceIQ.PriceIQ.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrderHistory(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }
}
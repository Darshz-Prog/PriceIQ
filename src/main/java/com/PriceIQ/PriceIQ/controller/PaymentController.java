package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.PaymentRequest;
import com.PriceIQ.PriceIQ.dto.response.PaymentResponse;
import com.PriceIQ.PriceIQ.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
}
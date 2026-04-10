package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.PaymentRequest;
import com.PriceIQ.PriceIQ.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);
}
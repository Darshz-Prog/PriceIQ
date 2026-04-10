package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.PaymentRequest;
import com.PriceIQ.PriceIQ.dto.response.PaymentResponse;
import com.PriceIQ.PriceIQ.entity.Order;
import com.PriceIQ.PriceIQ.entity.Payment;
import com.PriceIQ.PriceIQ.entity.PaymentStatus;
import com.PriceIQ.PriceIQ.exception.PaymentFailedException;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.OrderRepository;
import com.PriceIQ.PriceIQ.repository.PaymentRepository;
import com.PriceIQ.PriceIQ.service.NotificationService;
import com.PriceIQ.PriceIQ.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService { // ! NOT COMPLETED

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean paymentSuccess = true;

        if (!paymentSuccess) {
            throw new PaymentFailedException("Payment processing failed");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(UUID.randomUUID().toString())
                .paymentStatus(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        notificationService.sendPaymentSuccess(
                order.getUser().getId(),
                savedPayment.getId()
        );

        return PaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .orderId(order.getId())
                .transactionId(savedPayment.getTransactionId())
                .amount(savedPayment.getAmount())
                .paymentStatus(savedPayment.getPaymentStatus())
                .paymentMethod(savedPayment.getPaymentMethod())
                .createdAt(savedPayment.getCreatedAt())
                .build();
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
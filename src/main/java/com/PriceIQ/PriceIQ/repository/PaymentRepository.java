package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.Payment;
import com.PriceIQ.PriceIQ.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}
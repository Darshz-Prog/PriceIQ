package com.PriceIQ.PriceIQ.dto.response;

import com.PriceIQ.PriceIQ.entity.PaymentMethod;
import com.PriceIQ.PriceIQ.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse { //! NOT COMPLETED

    private Long paymentId;

    private Long orderId;

    private String transactionId;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    private String paymentGateway;

    private LocalDateTime createdAt;
}
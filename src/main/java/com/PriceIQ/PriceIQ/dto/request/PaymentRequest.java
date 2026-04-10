package com.PriceIQ.PriceIQ.dto.request;

import com.PriceIQ.PriceIQ.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotBlank
    private String transactionId;
}
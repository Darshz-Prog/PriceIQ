package com.PriceIQ.PriceIQ.dto.response;

import com.PriceIQ.PriceIQ.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long orderId;

    private String orderNumber;

    private OrderStatus orderStatus;

    private BigDecimal subtotal;

    private BigDecimal shippingCharge;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String shippingAddress;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String trackingNumber;

    private List<OrderItemResponse> items;

    private LocalDateTime createdAt;
}
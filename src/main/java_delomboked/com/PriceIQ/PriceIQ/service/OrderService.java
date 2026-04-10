package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.OrderRequest;
import com.PriceIQ.PriceIQ.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long orderId);

    List<OrderResponse> getOrderHistory(Long userId);
}
package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;

public interface KafkaProducerService {

    void publishUserEvent(EventRequest request);

    void publishPriceUpdate(Long productId, Double newPrice);

    void publishOrderCreated(Long orderId);
}
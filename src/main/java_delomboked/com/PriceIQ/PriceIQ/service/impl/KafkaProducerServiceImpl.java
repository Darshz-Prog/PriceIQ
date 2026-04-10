package com.PriceIQ.PriceIQ.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishUserEvent(EventRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("user-events", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user event");
        }
    }

    @Override
    public void publishPriceUpdate(Long productId, Double newPrice) {
        kafkaTemplate.send(
                "price-updates",
                "Product " + productId + " new price: " + newPrice
        );
    }

    @Override
    public void publishOrderCreated(Long orderId) {
        kafkaTemplate.send(
                "order-events",
                "Order created: " + orderId
        );
    }
}
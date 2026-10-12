package com.PriceIQ.PriceIQ.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private static final String USER_EVENTS_TOPIC = "user-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishUserEvent(EventRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(USER_EVENTS_TOPIC, String.valueOf(request.getUserId()), payload);
            log.info("Published event: {} for user={} product={}",
                    request.getEventType(), request.getUserId(), request.getProductId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user event: {}", e.getMessage());
            // Do NOT rethrow — Kafka failures must not break the primary business flow
        }
    }
}
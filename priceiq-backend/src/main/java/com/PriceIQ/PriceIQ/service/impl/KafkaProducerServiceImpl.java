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

            kafkaTemplate.send(
                    USER_EVENTS_TOPIC,
                    String.valueOf(request.getUserId()),
                    payload).whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error(
                                    "Kafka publish failed for event={}, product={}",
                                    request.getEventType(),
                                    request.getProductId(),
                                    exception);
                        } else {
                            log.info(
                                    "Kafka publish succeeded for event={}, product={}",
                                    request.getEventType(),
                                    request.getProductId());
                        }
                    });

        } catch (JsonProcessingException | RuntimeException e) {
            log.error(
                    "Unable to initiate Kafka publishing for product={}",
                    request.getProductId(),
                    e);
            // Do not let Kafka publishing failure break the product API.
        }
    }

}
package com.PriceIQ.PriceIQ.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "user-events", groupId = "priceiq-group")
    public void consumeUserEvents(String message) {
        log.info("Consumed user event: {}", message);
    }

    @KafkaListener(topics = "price-updates", groupId = "priceiq-group")
    public void consumePriceUpdates(String message) {
        log.info("Consumed price update: {}", message);
    }

    @KafkaListener(topics = "order-events", groupId = "priceiq-group")
    public void consumeOrderEvents(String message) {
        log.info("Consumed order event: {}", message);
    }
}
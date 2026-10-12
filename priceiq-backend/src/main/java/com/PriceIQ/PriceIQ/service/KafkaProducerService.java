package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;

public interface KafkaProducerService {

    void publishUserEvent(EventRequest request);
}
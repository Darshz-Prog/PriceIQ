package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.entity.UserEvent;
import com.PriceIQ.PriceIQ.repository.UserEventRepository;
import com.PriceIQ.PriceIQ.service.EventService;
import com.PriceIQ.PriceIQ.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserEventRepository userEventRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void trackEvent(EventRequest request) {

        UserEvent event = UserEvent.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .eventType(request.getEventType())
                .build();

        userEventRepository.save(event);

        kafkaProducerService.publishUserEvent(request);
    }
}
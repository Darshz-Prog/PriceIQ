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
    private final com.PriceIQ.PriceIQ.repository.UserRepository userRepository;

    @Override
    public void trackEvent(EventRequest request) {

        UserEvent event = UserEvent.builder()
                .user(request.getUserId() != null ? userRepository.findById(request.getUserId()).orElse(null) : null)
                .product(request.getProductId() != null ? com.PriceIQ.PriceIQ.entity.Product.builder().id(request.getProductId()).build() : null)
                .eventType(request.getEventType())
                .build();

        userEventRepository.save(event);

        kafkaProducerService.publishUserEvent(request);
    }
}
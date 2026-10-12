package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.entity.UserEvent;
import com.PriceIQ.PriceIQ.repository.UserEventRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.EventService;
import com.PriceIQ.PriceIQ.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserEventRepository userEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void trackEvent(EventRequest request) {
        // Build a lightweight reference — avoids a full product fetch
        Product productRef = request.getProductId() != null
                ? Product.builder().id(request.getProductId()).build()
                : null;

        User userRef = request.getUserId() != null
                ? userRepository.getReferenceById(request.getUserId())
                : null;

        UserEvent event = UserEvent.builder()
                .user(userRef)
                .product(productRef)
                .eventType(request.getEventType())
                .searchKeyword(request.getSearchKeyword())
                .sessionId(request.getSessionId())
                .metadata(request.getMetadata())
                .build();

        userEventRepository.save(event);

        // Publish to Kafka asynchronously; failures are logged but do not roll back the DB write
        kafkaProducerService.publishUserEvent(request);

        log.debug("Tracked event {} for user={}", request.getEventType(), request.getUserId());
    }
}
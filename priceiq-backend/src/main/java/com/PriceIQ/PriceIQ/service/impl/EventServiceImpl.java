package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.entity.UserEvent;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void trackEvent(EventRequest request) {
        if (request == null) {
            return;
        }

        try {
            Product productRef = request.getProductId() != null
                    ? productRepository.findById(request.getProductId()).orElse(null)
                    : null;

            User userRef = request.getUserId() != null
                    ? userRepository.findById(request.getUserId()).orElse(null)
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
            if (kafkaProducerService != null) {
                try {
                    kafkaProducerService.publishUserEvent(request);
                } catch (Exception e) {
                    log.warn("Kafka event publishing skipped/failed: {}", e.getMessage());
                }
            }

            log.debug("Tracked event {} for user={}", request.getEventType(), request.getUserId());
        } catch (Exception e) {
            log.warn("Could not track event {}: {}", request.getEventType(), e.getMessage());
        }
    }
}
package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.entity.UserEvent;
import com.PriceIQ.PriceIQ.repository.UserEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumer group: activity-db-service
 *
 * Responsibility: Persist every raw event to the user_events table.
 * This is the canonical event history — never delete from here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityConsumer {

    private static final String GROUP_ID = "activity-db-service";

    private final UserEventRepository userEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = GROUP_ID)
    @Transactional
    public void consume(String message) {
        try {
            EventRequest event = objectMapper.readValue(message, EventRequest.class);

            // Build lightweight references — we only need the IDs stored
            User userRef = event.getUserId() != null
                    ? User.builder().id(event.getUserId()).build()
                    : null;

            Product productRef = event.getProductId() != null
                    ? Product.builder().id(event.getProductId()).build()
                    : null;

            UserEvent userEvent = UserEvent.builder()
                    .user(userRef)
                    .product(productRef)
                    .eventType(event.getEventType())
                    .searchKeyword(event.getSearchKeyword())
                    .sessionId(event.getSessionId())
                    .metadata(event.getMetadata())
                    .build();

            userEventRepository.save(userEvent);

            log.debug("[activity-db-service] Stored event {} for user={}",
                    event.getEventType(), event.getUserId());

        } catch (Exception e) {
            log.error("[activity-db-service] Failed to process message: {} — {}", message, e.getMessage());
            // Don't rethrow — log and continue so the consumer doesn't stop
        }
    }
}

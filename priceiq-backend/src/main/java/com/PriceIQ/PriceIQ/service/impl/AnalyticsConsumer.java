package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.entity.ProductStatistic;
import com.PriceIQ.PriceIQ.repository.ProductStatisticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumer group: analytics-service
 *
 * Responsibility: Maintain pre-aggregated product-level statistics.
 * The GET /api/v1/analytics/products endpoint reads from product_statistics
 * without scanning the raw user_events table.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private static final String GROUP_ID = "analytics-service";

    private final ProductStatisticRepository productStatisticRepository;
    private final ObjectMapper               objectMapper;

    @KafkaListener(topics = "user-events", groupId = GROUP_ID)
    @Transactional
    public void consume(String message) {
        try {
            EventRequest event = objectMapper.readValue(message, EventRequest.class);

            if (event.getProductId() == null) {
                return; // SEARCH events carry no productId
            }

            ProductStatistic stat = productStatisticRepository
                    .findById(event.getProductId())
                    .orElseGet(() -> ProductStatistic.builder()
                            .productId(event.getProductId())
                            .build());

            switch (event.getEventType()) {
                case PRODUCT_VIEW, PRODUCT_CLICK ->
                        stat.setViewCount(stat.getViewCount() + 1);
                case ADD_TO_CART ->
                        stat.setCartCount(stat.getCartCount() + 1);
                case PURCHASE ->
                        stat.setPurchaseCount(stat.getPurchaseCount() + 1);
                default -> { /* Other events don't affect product statistics */ }
            }

            productStatisticRepository.save(stat);

            log.debug("[analytics-service] Updated stats for product={} event={}",
                    event.getProductId(), event.getEventType());

        } catch (Exception e) {
            log.error("[analytics-service] Failed to process message: {} — {}", message, e.getMessage());
        }
    }
}

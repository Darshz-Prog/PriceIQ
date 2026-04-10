package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.EventType;
import com.PriceIQ.PriceIQ.entity.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    List<UserEvent> findByUserId(Long userId);

    List<UserEvent> findByProductId(Long productId);

    List<UserEvent> findByEventType(EventType eventType);

    List<UserEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<UserEvent> findByUserIdAndEventType(Long userId, EventType eventType);
}
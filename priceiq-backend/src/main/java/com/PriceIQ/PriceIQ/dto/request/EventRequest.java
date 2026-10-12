package com.PriceIQ.PriceIQ.dto.request;

import com.PriceIQ.PriceIQ.entity.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
public class EventRequest {

    private Long userId;

    private EventType eventType;

    private Long productId;

    private String searchKeyword;

    private String sessionId;

    private String metadata;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
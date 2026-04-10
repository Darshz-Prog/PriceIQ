package com.PriceIQ.PriceIQ.dto.request;

import com.PriceIQ.PriceIQ.entity.EventType;
import lombok.Data;

@Data
public class EventRequest { //! NOT COMPLETED

    private Long userId;

    private EventType eventType;

    private Long productId;

    private String searchKeyword;

    private String sessionId;

    private String metadata;
}
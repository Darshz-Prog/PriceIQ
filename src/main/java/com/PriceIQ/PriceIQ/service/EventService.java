package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;

public interface EventService {

    void trackEvent(EventRequest request);
}
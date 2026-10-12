package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.dto.response.GenericSuccessResponse;
import com.PriceIQ.PriceIQ.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Manually track a user event (e.g. from the front-end for click tracking).
     * Most events are also published automatically from the service layer.
     */
    @PostMapping
    public ResponseEntity<GenericSuccessResponse> trackEvent(
            @Valid @RequestBody EventRequest request
    ) {
        eventService.trackEvent(request);
        return ResponseEntity.ok(new GenericSuccessResponse(true, "Event tracked successfully"));
    }
}
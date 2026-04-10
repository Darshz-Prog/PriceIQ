package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthResponse {

    private String service;

    private String status;

    private String database;

    private String redis;

    private String kafka;

    private String mlService;
}
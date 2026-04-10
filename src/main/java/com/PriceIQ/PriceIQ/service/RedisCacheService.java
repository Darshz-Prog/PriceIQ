package com.PriceIQ.PriceIQ.service;

import java.time.Duration;

public interface RedisCacheService { //! NOT COMPLETED

    void save(
            String key,
            Object value,
            Duration ttl
    );

    Object get(String key);

    void delete(String key);

    boolean exists(String key);
}
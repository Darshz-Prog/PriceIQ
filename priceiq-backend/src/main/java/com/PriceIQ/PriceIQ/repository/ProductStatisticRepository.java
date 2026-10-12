package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.ProductStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStatisticRepository
        extends JpaRepository<ProductStatistic, Long> {

    /** All product statistics ordered by view count descending (trending). */
    List<ProductStatistic> findAllByOrderByViewCountDesc();
}

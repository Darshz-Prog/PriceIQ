package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.PricingWeight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingWeightRepository extends JpaRepository<PricingWeight, Long> {

    Optional<PricingWeight> findByActiveTrue();
}
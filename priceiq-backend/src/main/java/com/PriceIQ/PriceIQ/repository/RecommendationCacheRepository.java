package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.RecommendationCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecommendationCacheRepository extends JpaRepository<RecommendationCache, Long> {

    Optional<RecommendationCache> findByUserIdAndRecommendationType(
            Long userId,
            String recommendationType
    );

    List<RecommendationCache> findByExpiresAtBefore(LocalDateTime dateTime);
}
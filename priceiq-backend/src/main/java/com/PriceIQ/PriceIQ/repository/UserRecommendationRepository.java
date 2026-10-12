package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRecommendationRepository
        extends JpaRepository<UserRecommendation, Long> {

    /** Returns stored recommendations for a user, best first. */
    List<UserRecommendation> findByUserIdOrderByRankAsc(Long userId);

    /** Wipe old recs before writing fresh ones during a scoring run. */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRecommendation r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);
}

package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.UserProductFeature;
import com.PriceIQ.PriceIQ.entity.UserProductFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProductFeatureRepository
        extends JpaRepository<UserProductFeature, UserProductFeatureId> {

    /** All feature rows for a user, ordered by view count (used for scoring). */
    List<UserProductFeature> findByUserIdOrderByViewCountDesc(Long userId);
}

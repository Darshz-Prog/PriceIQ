package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.AbTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbTestRepository extends JpaRepository<AbTest, Long> { //! NOT COMPLETED

    List<AbTest> findByActiveTrue();

    List<AbTest> findByTestNameContainingIgnoreCase(String keyword);
}
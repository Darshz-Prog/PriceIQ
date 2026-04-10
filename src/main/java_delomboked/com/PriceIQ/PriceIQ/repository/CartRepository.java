package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndActiveTrue(Long userId);

    List<Cart> findByUserId(Long userId);
}
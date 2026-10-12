package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.Order;
import com.PriceIQ.PriceIQ.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
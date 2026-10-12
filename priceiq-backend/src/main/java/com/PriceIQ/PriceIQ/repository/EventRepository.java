package com.PriceIQ.PriceIQ.repository;


import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<UserEvent, Long> {

    @Query("SELECT e.product FROM UserEvent e WHERE e.eventType = 'PRODUCT_VIEW' GROUP BY e.product ORDER BY COUNT(e) DESC")
    List<Product> findTrendingProducts();

}

package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryAndActiveTrue(String category);

    org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCase(String keyword, org.springframework.data.domain.Pageable pageable);
}
package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryAndActiveTrue(String category);

    /**
     * Returns active, purchasable products in a category.
     * Used by similar-products and FBT fallback to avoid N+1 filtering.
     */
    List<Product> findByCategoryAndActiveTrueAndStockQuantityGreaterThan(String category, int minStock);

    org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCase(String keyword, org.springframework.data.domain.Pageable pageable);
}
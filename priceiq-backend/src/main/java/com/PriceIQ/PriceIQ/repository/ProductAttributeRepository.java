package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    List<ProductAttribute> findByProductId(Long productId);

    List<ProductAttribute> findByAttributeName(String attributeName);
}
package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.ProductRequest;
import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String category,
            String brand,
            Double minPrice,
            Double maxPrice,
            String sortBy
    );

    ProductResponse getProductById(Long productId);

    Page<ProductResponse> searchProducts(
            String keyword,
            int page,
            int size
    );

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    void deleteProduct(Long productId);
}
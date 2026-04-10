package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.ProductRequest;
import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String category,
            String brand,
            Double minPrice,
            Double maxPrice,
            String sortBy
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy == null ? "createdAt" : sortBy).descending()
        );

        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return mapToResponse(product);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .findByNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .category(request.getCategory())
                .basePrice(request.getBasePrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setBasePrice(request.getBasePrice());
        product.setStockQuantity(request.getStockQuantity());

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .basePrice(product.getBasePrice())
                .currentPrice(product.getCurrentPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
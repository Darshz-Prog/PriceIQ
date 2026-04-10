package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.ProductImageRequest;
import com.PriceIQ.PriceIQ.dto.request.ProductRequest;
import com.PriceIQ.PriceIQ.dto.response.ProductImageResponse;
import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.ProductAttribute;
import com.PriceIQ.PriceIQ.entity.ProductImage;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

        List<ProductImage> images = request.getImages() == null ? List.of()
                 : request.getImages()  
                .stream()
                .map(image -> ProductImage.builder()
                        .imageUrl(image.getImageUrl())
                        .primaryImage(image.getPrimaryImage())
                        .displayOrder(image.getDisplayOrder())
                        .altText(image.getAltText())
                        .build())
                .toList();

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku()) //? what is sku ? idk
                .brand(request.getBrand())
                .category(request.getCategory())
                .basePrice(request.getBasePrice())
                .currentPrice(request.getCurrentPrice())
                .active(request.getActive())
                .stockQuantity(request.getStockQuantity())
                .build();

        images.forEach(image -> image.setProduct(product));
        product.setImages(images);

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

        List<ProductImageResponse> images = product.getImages() == null
            ? List.of()
            : product.getImages()
                    .stream()
                    .map(image -> ProductImageResponse.builder()
                            .id(image.getId())
                            .imageUrl(image.getImageUrl())
                            .primaryImage(image.getPrimaryImage())
                            .displayOrder(image.getDisplayOrder())
                            .altText(image.getAltText())
                            .build())
                    .toList();



        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .basePrice(product.getBasePrice())
                .currentPrice(product.getCurrentPrice())
                .stockQuantity(product.getStockQuantity())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .images(images)
                .build();

                // why we are not sending image URL to reponce ?
                //? because we are storing the image URL in image DB and we are not storing the image URL in current DB.
                // to get image URL we need do is call image DB API and get the image URL.
                // How we call image URL ?
                //? we call api by List<pImage> = imageDBService.getImagesByProductId(product.getId());
                //? then we map the image URL to the response.
                //? List<String> imageUrls = imageDBService.getImagesByProductId(product.getId())
                //?         .stream()
                //?         .map(pImage::getImageUrl)
                //?         .collect(Collectors.toList());
                //? response.setImageUrls(imageUrls);
    }
}
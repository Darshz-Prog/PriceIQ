package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.response.AnalyticsResponse;
import com.PriceIQ.PriceIQ.dto.response.ProductImageResponse;
import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.repository.EventRepository;
import com.PriceIQ.PriceIQ.repository.OrderRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserEventRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.AnalyticsService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserEventRepository userEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    @Override

    public AnalyticsResponse getDashboardAnalytics() {

        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long totalEvents = userEventRepository.count();
        long totalUsers = userRepository.count();

        return AnalyticsResponse.builder()
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .totalEvents(totalEvents)
                .totalUsers(totalUsers)
                .build();
    }

    @Override
    public List<ProductResponse> getTrendingProducts() {
        return eventRepository
                .findTrendingProducts() //! for now its by view count
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Double getConversionRate() {

        long totalOrders = orderRepository.count();
        long totalEvents = userEventRepository.count();

        if (totalEvents == 0) {
            return 0.0;
        }

        return (double) totalOrders / totalEvents * 100;
    }

    @Override
    public Map<String, Object> getAdminMetrics() {
//!
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("totalOrders", orderRepository.count());
        metrics.put("totalProducts", productRepository.count());
        metrics.put("totalEvents", userEventRepository.count());
        metrics.put("conversionRate", getConversionRate());

        return metrics;
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
    }
}
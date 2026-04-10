package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.OrderRequest;
import com.PriceIQ.PriceIQ.dto.response.OrderResponse;
import com.PriceIQ.PriceIQ.entity.Order;
import com.PriceIQ.PriceIQ.entity.OrderItem;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.OrderRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.KafkaProducerService;
import com.PriceIQ.PriceIQ.service.NotificationService;
import com.PriceIQ.PriceIQ.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;
    private final NotificationService notificationService;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = Order.builder()
                .user(user)
                .status("PLACED")
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemRequest.getQuantity())
                            .price(product.getPrice())
                            .build();
                })
                .toList();

        double totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        kafkaProducerService.publishOrderCreated(savedOrder.getId());
        notificationService.sendOrderConfirmation(user.getId(), savedOrder.getId());

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getOrderHistory(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems())
                .build();
    }
}
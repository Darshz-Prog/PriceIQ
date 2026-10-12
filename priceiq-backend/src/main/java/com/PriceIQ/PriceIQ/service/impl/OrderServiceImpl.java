package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.dto.request.OrderRequest;
import com.PriceIQ.PriceIQ.dto.response.OrderResponse;
import com.PriceIQ.PriceIQ.entity.Cart;
import com.PriceIQ.PriceIQ.entity.EventType;
import com.PriceIQ.PriceIQ.entity.Order;
import com.PriceIQ.PriceIQ.entity.OrderItem;
import com.PriceIQ.PriceIQ.entity.OrderStatus;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.OrderRepository;
import com.PriceIQ.PriceIQ.repository.CartRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.EventService;
import com.PriceIQ.PriceIQ.service.NotificationService;
import com.PriceIQ.PriceIQ.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final NotificationService notificationService;
        private final EventService eventService;
        private final CartRepository cartRepository;

        @Override
        public OrderResponse createOrder(OrderRequest request) {

                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Order order = Order.builder()
                                .user(user)
                                .orderStatus(OrderStatus.PENDING)
                                .orderNumber("ORD-" + System.currentTimeMillis())
                                .shippingAddress(request.getShippingAddress() != null ? request.getShippingAddress()
                                                : "N/A")
                                .city(request.getCity() != null ? request.getCity() : "N/A")
                                .state(request.getState() != null ? request.getState() : "N/A")
                                .country(request.getCountry() != null ? request.getCountry() : "N/A")
                                .postalCode(request.getPostalCode() != null ? request.getPostalCode() : "N/A")
                                .subtotal(BigDecimal.ZERO)
                                .shippingCharge(BigDecimal.ZERO)
                                .taxAmount(BigDecimal.ZERO)
                                .discountAmount(BigDecimal.ZERO)
                                .createdAt(LocalDateTime.now())
                                .build();

                Cart cart = cartRepository.findByUserId(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

                List<OrderItem> items = cart.getCartItems().stream()
                                .map(cartItem -> {

                                        Product product = cartItem.getProduct();

                                        BigDecimal price = product.getBasePrice() != null
                                                        ? product.getBasePrice()
                                                        : BigDecimal.ZERO;

                                        BigDecimal itemTotal = price.multiply(
                                                        BigDecimal.valueOf(cartItem.getQuantity()));

                                        return OrderItem.builder()
                                                        .order(order)
                                                        .product(product)
                                                        .productName(product.getName())
                                                        .quantity(cartItem.getQuantity())
                                                        .pricePerUnit(price)
                                                        .totalPrice(itemTotal)
                                                        .build();
                                })
                                .toList();

                BigDecimal totalAmount = items.stream()
                                .map(OrderItem::getTotalPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                order.setOrderItems(items);
                order.setTotalAmount(totalAmount);
                order.setSubtotal(totalAmount); // ! simplified(we can add discount )

                Order savedOrder = orderRepository.save(order);

                // Fire one PURCHASE event per order item
                savedOrder.getOrderItems().forEach(item -> eventService.trackEvent(EventRequest.builder()
                                .userId(user.getId())
                                .productId(item.getProduct().getId())
                                .eventType(EventType.PURCHASE)
                                .build()));

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
                                .orderStatus(order.getOrderStatus())
                                .totalAmount(order.getTotalAmount())
                                .createdAt(order.getCreatedAt())
                                .items(Collections.emptyList())
                                .build();
        }
}
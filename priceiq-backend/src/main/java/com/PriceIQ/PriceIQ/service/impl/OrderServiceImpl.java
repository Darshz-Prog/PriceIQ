package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.dto.request.OrderRequest;
import com.PriceIQ.PriceIQ.dto.response.OrderItemResponse;
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

import com.PriceIQ.PriceIQ.repository.CartItemRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final NotificationService notificationService;
        private final EventService eventService;
        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;

        @Override
        public OrderResponse createOrder(OrderRequest request) {

                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Cart cart = cartRepository.findFirstByUserIdAndActiveTrue(request.getUserId())
                                .or(() -> cartRepository.findFirstByUserIdOrderByIdDesc(request.getUserId()))
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

                List<com.PriceIQ.PriceIQ.entity.CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
                if (cartItems.isEmpty()) {
                        throw new com.PriceIQ.PriceIQ.exception.InvalidRequestException("Cannot place an order with an empty cart");
                }

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

                List<OrderItem> items = cartItems.stream()
                                .map(cartItem -> {

                                        Product product = cartItem.getProduct();

                                        BigDecimal price = cartItem.getPriceAtAddition() != null
                                                        ? cartItem.getPriceAtAddition()
                                                        : (product != null && product.getCurrentPrice() != null
                                                                        ? product.getCurrentPrice()
                                                                        : (product != null && product.getBasePrice() != null ? product.getBasePrice() : BigDecimal.ZERO));

                                        BigDecimal itemTotal = price.multiply(
                                                        BigDecimal.valueOf(cartItem.getQuantity()));

                                        return OrderItem.builder()
                                                        .order(order)
                                                        .product(product)
                                                        .productName(product != null ? product.getName() : "Unknown Product")
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
                order.setSubtotal(totalAmount);

                Order savedOrder = orderRepository.save(order);

                // Clear the cart after successfully placing order
                cartItemRepository.deleteByCartId(cart.getId());
                cart.setTotalAmount(BigDecimal.ZERO);
                cartRepository.save(cart);

                // Fire events and notifications safely
                try {
                        savedOrder.getOrderItems().forEach(item -> {
                                if (item.getProduct() != null) {
                                        eventService.trackEvent(EventRequest.builder()
                                                        .userId(user.getId())
                                                        .productId(item.getProduct().getId())
                                                        .eventType(EventType.PURCHASE)
                                                        .build());
                                }
                        });
                } catch (Exception ignored) {
                }

                try {
                        notificationService.sendOrderConfirmation(user.getId(), savedOrder.getId());
                } catch (Exception ignored) {
                }

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

                List<OrderItemResponse> itemResponses = order.getOrderItems() == null
                                ? Collections.emptyList()
                                : order.getOrderItems().stream()
                                                .map(item -> OrderItemResponse.builder()
                                                                .orderItemId(item.getId())
                                                                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                                                                .productName(item.getProductName())
                                                                .quantity(item.getQuantity())
                                                                .pricePerUnit(item.getPricePerUnit())
                                                                .totalPrice(item.getTotalPrice())
                                                                .build())
                                                .toList();

                return OrderResponse.builder()
                                .orderId(order.getId())
                                .userId(order.getUser().getId())
                                .orderNumber(order.getOrderNumber())
                                .orderStatus(order.getOrderStatus())
                                .totalAmount(order.getTotalAmount())
                                .subtotal(order.getSubtotal())
                                .shippingCharge(order.getShippingCharge())
                                .taxAmount(order.getTaxAmount())
                                .discountAmount(order.getDiscountAmount())
                                .shippingAddress(order.getShippingAddress())
                                .city(order.getCity())
                                .state(order.getState())
                                .country(order.getCountry())
                                .postalCode(order.getPostalCode())
                                .trackingNumber(order.getTrackingNumber())
                                .createdAt(order.getCreatedAt())
                                .items(itemResponses)
                                .build();
        }
}
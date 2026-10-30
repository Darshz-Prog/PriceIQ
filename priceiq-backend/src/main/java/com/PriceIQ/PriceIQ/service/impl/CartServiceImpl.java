package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.CartRequest;
import com.PriceIQ.PriceIQ.dto.request.EventRequest;
import com.PriceIQ.PriceIQ.dto.response.CartItemResponse;
import com.PriceIQ.PriceIQ.dto.response.CartResponse;
import com.PriceIQ.PriceIQ.entity.Cart;
import com.PriceIQ.PriceIQ.entity.CartItem;
import com.PriceIQ.PriceIQ.entity.EventType;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.CartItemRepository;
import com.PriceIQ.PriceIQ.repository.CartRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.CartService;
import com.PriceIQ.PriceIQ.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final EventService eventService;

        private BigDecimal recalculateCartTotal(Cart cart) {
                List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
                BigDecimal total = items.stream()
                                .map(item -> {
                                        BigDecimal price = item.getPriceAtAddition() != null
                                                        ? item.getPriceAtAddition()
                                                        : (item.getProduct() != null && item.getProduct().getCurrentPrice() != null
                                                                        ? item.getProduct().getCurrentPrice()
                                                                        : BigDecimal.ZERO);
                                        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                cart.setTotalAmount(total);
                cartRepository.save(cart);
                return total;
        }

        private Cart getOrCreateActiveCart(Long userId) {
                return cartRepository.findFirstByUserIdAndActiveTrue(userId)
                                .or(() -> cartRepository.findFirstByUserIdOrderByIdDesc(userId))
                                .orElseGet(() -> {
                                        User user = userRepository.findById(userId)
                                                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                                        return cartRepository.save(Cart.builder()
                                                        .user(user)
                                                        .active(true)
                                                        .totalAmount(BigDecimal.ZERO)
                                                        .build());
                                });
        }

        @Override
        public CartResponse getCartByUserId(Long userId) {
                Cart cart = getOrCreateActiveCart(userId);

                List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

                List<CartItemResponse> itemResponses = cartItems.stream().map(item -> {
                        Product p = item.getProduct();

                        String imageUrl = (p != null && p.getImages() != null && !p.getImages().isEmpty())
                                        ? p.getImages().get(0).getImageUrl()
                                        : null;

                        BigDecimal price = item.getPriceAtAddition() != null
                                        ? item.getPriceAtAddition()
                                        : (p != null && p.getCurrentPrice() != null ? p.getCurrentPrice() : BigDecimal.ZERO);

                        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(item.getQuantity()));

                        return CartItemResponse.builder()
                                        .cartItemId(item.getId())
                                        .productId(p != null ? p.getId() : null)
                                        .productName(p != null ? p.getName() : "Unknown Product")
                                        .productImage(imageUrl)
                                        .quantity(item.getQuantity())
                                        .priceAtAddition(price)
                                        .totalPrice(totalPrice)
                                        .build();
                }).collect(Collectors.toList());

                BigDecimal calculatedTotal = itemResponses.stream()
                                .map(CartItemResponse::getTotalPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (cart.getTotalAmount() == null || cart.getTotalAmount().compareTo(calculatedTotal) != 0) {
                        cart.setTotalAmount(calculatedTotal);
                        cartRepository.save(cart);
                }

                return CartResponse.builder()
                                .cartId(cart.getId())
                                .userId(userId)
                                .items(itemResponses)
                                .totalAmount(calculatedTotal)
                                .build();
        }

        @Override
        public CartResponse addToCart(CartRequest request) {
                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                if (Boolean.FALSE.equals(product.getActive())) {
                        throw new com.PriceIQ.PriceIQ.exception.InvalidRequestException("Product is inactive and cannot be purchased");
                }

                if (product.getStockQuantity() != null && product.getStockQuantity() <= 0) {
                        throw new com.PriceIQ.PriceIQ.exception.InvalidRequestException("Product is out of stock");
                }

                Cart cart = getOrCreateActiveCart(user.getId());

                CartItem item = cartItemRepository
                                .findByCartIdAndProductId(cart.getId(), product.getId())
                                .orElse(null);

                BigDecimal productPrice = product.getCurrentPrice() != null
                                ? product.getCurrentPrice()
                                : (product.getBasePrice() != null ? product.getBasePrice() : BigDecimal.ZERO);

                if (item != null) {
                        item.setQuantity(item.getQuantity() + request.getQuantity());
                        if (item.getPriceAtAddition() == null) {
                                item.setPriceAtAddition(productPrice);
                        }
                } else {
                        item = CartItem.builder()
                                        .cart(cart)
                                        .product(product)
                                        .quantity(request.getQuantity())
                                        .priceAtAddition(productPrice)
                                        .build();
                }

                cartItemRepository.save(item);
                cartItemRepository.flush();

                recalculateCartTotal(cart);

                // Fire ADD_TO_CART event safely
                try {
                        eventService.trackEvent(EventRequest.builder()
                                        .userId(user.getId())
                                        .productId(product.getId())
                                        .eventType(EventType.ADD_TO_CART)
                                        .build());
                } catch (Exception ignored) {
                }

                return getCartByUserId(user.getId());
        }

        @Override
        public CartResponse removeFromCart(Long cartItemId) {
                CartItem cartItem = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

                Cart cart = cartItem.getCart();
                Long userId = cart.getUser().getId();
                Long productId = cartItem.getProduct() != null ? cartItem.getProduct().getId() : null;

                cartItemRepository.delete(cartItem);
                cartItemRepository.flush();

                recalculateCartTotal(cart);

                // Fire REMOVE_FROM_CART event safely
                try {
                        if (productId != null) {
                                eventService.trackEvent(EventRequest.builder()
                                                .userId(userId)
                                                .productId(productId)
                                                .eventType(EventType.REMOVE_FROM_CART)
                                                .build());
                        }
                } catch (Exception ignored) {
                }

                return getCartByUserId(userId);
        }

        @Override
        public CartResponse updateQuantity(Long cartItemId, Integer quantity) {
                CartItem cartItem = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

                if (quantity == null || quantity <= 0) {
                        return removeFromCart(cartItemId);
                }

                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
                cartItemRepository.flush();

                recalculateCartTotal(cartItem.getCart());

                return getCartByUserId(cartItem.getCart().getUser().getId());
        }

        @Override
        public void clearCart(Long userId) {
                Cart cart = cartRepository.findFirstByUserIdAndActiveTrue(userId)
                                .or(() -> cartRepository.findFirstByUserIdOrderByIdDesc(userId))
                                .orElse(null);

                if (cart != null) {
                        cartItemRepository.deleteByCartId(cart.getId());
                        cart.setTotalAmount(BigDecimal.ZERO);
                        cartRepository.save(cart);
                }
        }
}
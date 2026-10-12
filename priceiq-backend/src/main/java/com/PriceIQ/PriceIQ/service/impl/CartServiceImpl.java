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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
        // ! THE ISSUE IN CART AND CARTITEM 'S Primary key is or something is malfunctioning
        // working
        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final EventService eventService;

        @Override
        public CartResponse getCartByUserId(Long userId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

                List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId()); // We cannot do direct cartItems = cart.getCartItems() because it will lead to infinite recursion

                List<CartItemResponse> itemResponses = cartItems.stream().map(item -> {
                        Product p = item.getProduct();

                        String imageUrl = (p.getImages() != null && !p.getImages().isEmpty())
                                        ? p.getImages().get(0).getImageUrl()
                                        : null;

                        BigDecimal price = item.getPriceAtAddition() != null
                                        ? item.getPriceAtAddition()
                                        : p.getCurrentPrice();

                        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(item.getQuantity()));

                        return CartItemResponse.builder()
                                        .cartItemId(item.getId())
                                        .productId(p.getId())
                                        .productName(p.getName())
                                        .productImage(imageUrl)
                                        .quantity(item.getQuantity())
                                        .priceAtAddition(price)
                                        .totalPrice(totalPrice)
                                        .build();
                }).collect(Collectors.toList());

                return CartResponse.builder()
                                .cartId(cart.getId())
                                .userId(userId)
                                .items(itemResponses)
                                .totalAmount(cart.getTotalAmount())
                                .build();
        }

        @Override
        public CartResponse addToCart(CartRequest request) {

                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                Cart cart = cartRepository.findByUserId(user.getId())
                                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

                CartItem item = cartItemRepository
                                .findByCartIdAndProductId(cart.getId(), product.getId())
                                .orElse(null);

                if (item != null) {
                        item.setQuantity(item.getQuantity() + request.getQuantity());
                } else {
                        item = CartItem.builder()
                                        .cart(cart)
                                        .product(product)
                                        .quantity(request.getQuantity())
                                        .priceAtAddition(product.getCurrentPrice())
                                        .build();
                }

                cartItemRepository.save(item);

                // Fire ADD_TO_CART event
                eventService.trackEvent(EventRequest.builder()
                                .userId(user.getId())
                                .productId(product.getId())
                                .eventType(EventType.ADD_TO_CART)
                                .build());

                List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

                BigDecimal total = items.stream()
                                .map(cartItem -> cartItem.getPriceAtAddition()
                                                .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                cart.setTotalAmount(total);
                cartRepository.save(cart);

                return getCartByUserId(user.getId());
        }

        @Override
        public CartResponse removeFromCart(Long cartItemId) {
                CartItem cartItem = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

                Long userId = cartItem.getCart().getUser().getId();
                Long productId = cartItem.getProduct().getId();

                cartItemRepository.delete(cartItem);

                // Fire REMOVE_FROM_CART event
                eventService.trackEvent(EventRequest.builder()
                                .userId(userId)
                                .productId(productId)
                                .eventType(EventType.REMOVE_FROM_CART)
                                .build());

                return getCartByUserId(userId);
        }

        @Override
        public CartResponse updateQuantity(Long cartItemId, Integer quantity) {
                CartItem cartItem = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);

                return getCartByUserId(cartItem.getCart().getUser().getId());
        }

        @Override
        public void clearCart(Long userId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

                cartItemRepository.deleteAll(cart.getCartItems());

                cart.setTotalAmount(BigDecimal.ZERO);
                cart.setCartItems(new ArrayList<>());

                cartRepository.save(cart);
        }
}
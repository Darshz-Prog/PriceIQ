package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.CartRequest;
import com.PriceIQ.PriceIQ.dto.response.CartItemResponse;
import com.PriceIQ.PriceIQ.dto.response.CartResponse;
import com.PriceIQ.PriceIQ.entity.Cart;
import com.PriceIQ.PriceIQ.entity.CartItem;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.CartItemRepository;
import com.PriceIQ.PriceIQ.repository.CartRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;

        @Override
        public CartResponse getCartByUserId(Long userId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

                List<CartItemResponse> itemResponses = cart.getCartItems().stream().map(item -> {
                        Product p = item.getProduct();
                        String imageUrl = (p.getImages() != null && !p.getImages().isEmpty())
                                        ? p.getImages().get(0).getImageUrl()
                                        : null;
                        BigDecimal price = item.getPriceAtAddition() != null ? item.getPriceAtAddition()
                                        : p.getCurrentPrice();
                        BigDecimal totalPrice = price != null ? price.multiply(BigDecimal.valueOf(item.getQuantity()))
                                        : BigDecimal.ZERO;

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

                CartItem item = CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(request.getQuantity())
                                .build();

                cartItemRepository.save(item);

                return getCartByUserId(user.getId());
        }

        @Override
        public CartResponse removeFromCart(Long cartItemId) {
                CartItem cartItem = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

                Long userId = cartItem.getCart().getUser().getId();

                cartItemRepository.delete(cartItem);

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
                cartItemRepository.deleteAllByCartUserId(userId);
        }
}
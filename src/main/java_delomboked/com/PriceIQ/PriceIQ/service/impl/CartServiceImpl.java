package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.CartRequest;
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

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(userId)
                .items(cart.getItems())
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
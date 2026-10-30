package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.request.CartRequest;
import com.PriceIQ.PriceIQ.dto.response.CartResponse;
import com.PriceIQ.PriceIQ.entity.Cart;
import com.PriceIQ.PriceIQ.entity.CartItem;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.repository.CartItemRepository;
import com.PriceIQ.PriceIQ.repository.CartRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        product = Product.builder()
                .id(10L)
                .name("Keyboard")
                .currentPrice(BigDecimal.valueOf(100.00))
                .basePrice(BigDecimal.valueOf(100.00))
                .build();
        cart = Cart.builder()
                .id(20L)
                .user(user)
                .active(true)
                .totalAmount(BigDecimal.ZERO)
                .build();
        cartItem = CartItem.builder()
                .id(30L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .priceAtAddition(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    void addToCart_ShouldAddItemAndRecalculateTotal() {
        CartRequest request = new CartRequest();
        request.setUserId(1L);
        request.setProductId(10L);
        request.setQuantity(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findFirstByUserIdAndActiveTrue(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(20L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemRepository.findByCartId(20L)).thenReturn(List.of(cartItem));

        CartResponse response = cartService.addToCart(request);

        assertNotNull(response);
        assertEquals(20L, response.getCartId());
        assertEquals(0, new BigDecimal("200.00").compareTo(response.getTotalAmount()));
        assertEquals(1, response.getItems().size());
        assertEquals(0, new BigDecimal("200.00").compareTo(response.getItems().get(0).getTotalPrice()));
    }

    @Test
    void removeFromCart_ShouldDeleteAndRecalculateTotal() {
        when(cartItemRepository.findById(30L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findFirstByUserIdAndActiveTrue(1L)).thenReturn(Optional.of(cart));
        // After deletion, cart items list is empty
        when(cartItemRepository.findByCartId(20L)).thenReturn(new ArrayList<>());

        CartResponse response = cartService.removeFromCart(30L);

        assertNotNull(response);
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getTotalAmount()));
        assertTrue(response.getItems().isEmpty());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void updateQuantity_ShouldUpdateAndRecalculateTotal() {
        when(cartItemRepository.findById(30L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findFirstByUserIdAndActiveTrue(1L)).thenReturn(Optional.of(cart));

        CartItem updatedItem = CartItem.builder()
                .id(30L)
                .cart(cart)
                .product(product)
                .quantity(3)
                .priceAtAddition(BigDecimal.valueOf(100.00))
                .build();

        when(cartItemRepository.findByCartId(20L)).thenReturn(List.of(updatedItem));

        CartResponse response = cartService.updateQuantity(30L, 3);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("300.00").compareTo(response.getTotalAmount()));
        assertEquals(3, response.getItems().get(0).getQuantity());
    }

    @Test
    void addToCart_ShouldFail_WhenProductIsInactive() {
        Product inactiveProduct = Product.builder()
                .id(11L)
                .name("Old Product")
                .active(false)
                .stockQuantity(10)
                .currentPrice(BigDecimal.valueOf(50.00))
                .build();

        CartRequest request = new CartRequest();
        request.setUserId(1L);
        request.setProductId(11L);
        request.setQuantity(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(11L)).thenReturn(Optional.of(inactiveProduct));

        assertThrows(com.PriceIQ.PriceIQ.exception.InvalidRequestException.class, () -> {
            cartService.addToCart(request);
        });
    }

    @Test
    void addToCart_ShouldFail_WhenProductHasZeroStock() {
        Product outOfStockProduct = Product.builder()
                .id(12L)
                .name("Sold Out Product")
                .active(true)
                .stockQuantity(0)
                .currentPrice(BigDecimal.valueOf(50.00))
                .build();

        CartRequest request = new CartRequest();
        request.setUserId(1L);
        request.setProductId(12L);
        request.setQuantity(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(12L)).thenReturn(Optional.of(outOfStockProduct));

        assertThrows(com.PriceIQ.PriceIQ.exception.InvalidRequestException.class, () -> {
            cartService.addToCart(request);
        });
    }
}

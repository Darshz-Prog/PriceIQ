package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.service.impl.ProductServiceImpl;
import com.PriceIQ.PriceIQ.util.UserContextHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserContextHelper userContextHelper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        product = Product.builder()
                .id(4L)
                .name("iPhone 15")
                .sku("IPHONE-15-128")
                .brand("Apple")
                .category("Smartphones")
                .basePrice(BigDecimal.valueOf(79999.00))
                .currentPrice(BigDecimal.valueOf(74999.00))
                .stockQuantity(10)
                .active(true)
                .averageRating(4.8)
                .totalReviews(25)
                .build();
    }

    @Test
    void getProductById_ShouldReturnProductWithActiveTrueAndSku() {
        when(productRepository.findById(4L)).thenReturn(Optional.of(product));
        when(userContextHelper.getCurrentUserId()).thenReturn(Optional.empty());

        ProductResponse response = productService.getProductById(4L);

        assertNotNull(response);
        assertEquals(4L, response.getId());
        assertEquals("iPhone 15", response.getName());
        assertEquals("IPHONE-15-128", response.getSku());
        assertEquals(10, response.getStockQuantity());
        assertNotNull(response.getActive(), "ProductResponse.active must not be null");
        assertTrue(response.getActive(), "ProductResponse.active must be true");
    }

    @Test
    void productResponse_ShouldSerializeActiveFieldInJson() throws Exception {
        when(productRepository.findById(4L)).thenReturn(Optional.of(product));
        when(userContextHelper.getCurrentUserId()).thenReturn(Optional.empty());

        ProductResponse response = productService.getProductById(4L);
        String json = objectMapper.writeValueAsString(response);

        assertTrue(json.contains("\"active\":true"), "Serialized JSON must contain \"active\":true. Got: " + json);
        assertTrue(json.contains("\"sku\":\"IPHONE-15-128\""), "Serialized JSON must contain sku");
    }

    @Test
    void getProductById_WhenInactive_ShouldReturnActiveFalse() throws Exception {
        product.setActive(false);
        when(productRepository.findById(4L)).thenReturn(Optional.of(product));
        when(userContextHelper.getCurrentUserId()).thenReturn(Optional.empty());

        ProductResponse response = productService.getProductById(4L);

        assertNotNull(response);
        assertNotNull(response.getActive());
        assertFalse(response.getActive());

        String json = objectMapper.writeValueAsString(response);
        assertTrue(json.contains("\"active\":false"), "Serialized JSON must contain \"active\":false");
    }
}

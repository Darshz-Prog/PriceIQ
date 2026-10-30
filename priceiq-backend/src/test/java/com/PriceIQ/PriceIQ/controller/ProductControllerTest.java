package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getProductById_ShouldReturnProductWithActiveTrue() {
        ProductResponse mockResponse = ProductResponse.builder()
                .id(4L)
                .name("iPhone 15")
                .sku("IPHONE-15-128")
                .stockQuantity(10)
                .active(true)
                .basePrice(BigDecimal.valueOf(79999.00))
                .currentPrice(BigDecimal.valueOf(74999.00))
                .build();

        when(productService.getProductById(4L)).thenReturn(mockResponse);

        ResponseEntity<ProductResponse> responseEntity = productController.getProductById(4L);

        assertNotNull(responseEntity.getBody());
        assertEquals(4L, responseEntity.getBody().getId());
        assertEquals("iPhone 15", responseEntity.getBody().getName());
        assertEquals(Boolean.TRUE, responseEntity.getBody().getActive());
        assertEquals(10, responseEntity.getBody().getStockQuantity());
    }
}

package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.service.RecommendationService;
import com.PriceIQ.PriceIQ.util.UserContextHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private UserContextHelper userContextHelper;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void getFeed_ShouldReturnUserPersonalizedFeed() {
        ProductResponse product = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .currentPrice(BigDecimal.valueOf(100))
                .build();

        when(userContextHelper.getCurrentUserId()).thenReturn(Optional.of(42L));
        when(recommendationService.getFeedForUser(42L)).thenReturn(Collections.singletonList(product));

        ResponseEntity<List<ProductResponse>> response = recommendationController.getFeed();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        verify(recommendationService, times(1)).getFeedForUser(42L);
    }

    @Test
    void getSimilar_ShouldClampLimitAndReturnProducts() {
        ProductResponse product = ProductResponse.builder().id(2L).name("Similar Product").build();
        when(recommendationService.getSimilarProducts(eq(1L), eq(20))).thenReturn(Collections.singletonList(product));

        // Limit 100 should be clamped to 20
        ResponseEntity<List<ProductResponse>> response = recommendationController.getSimilar(1L, 100);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(recommendationService, times(1)).getSimilarProducts(1L, 20);
    }

    @Test
    void getFrequentlyBoughtTogether_ShouldCallService() {
        ProductResponse product = ProductResponse.builder().id(3L).name("FBT Product").build();
        when(recommendationService.getFrequentlyBoughtTogether(eq(1L), eq(6))).thenReturn(Collections.singletonList(product));

        ResponseEntity<List<ProductResponse>> response = recommendationController.getFrequentlyBoughtTogether(1L, 6);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(recommendationService, times(1)).getFrequentlyBoughtTogether(1L, 6);
    }

    @Test
    void getTrending_ShouldCallServiceWithClampedLimit() {
        ProductResponse product = ProductResponse.builder().id(5L).name("Trending Item").build();
        when(recommendationService.getTrendingProducts(12)).thenReturn(Collections.singletonList(product));

        ResponseEntity<List<ProductResponse>> response = recommendationController.getTrending(12);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(recommendationService, times(1)).getTrendingProducts(12);
    }
}

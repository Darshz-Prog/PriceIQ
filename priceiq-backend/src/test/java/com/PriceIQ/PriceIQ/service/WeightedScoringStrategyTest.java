package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.ProductStatistic;
import com.PriceIQ.PriceIQ.entity.UserProductFeature;
import com.PriceIQ.PriceIQ.repository.ProductStatisticRepository;
import com.PriceIQ.PriceIQ.service.impl.WeightedScoringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeightedScoringStrategyTest {

    @Mock
    private ProductStatisticRepository productStatisticRepository;

    @InjectMocks
    private WeightedScoringStrategy scoringStrategy;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder().id(10L).name("Test Product").build();
    }

    @Test
    void score_ShouldCombineInteractionsRecencyAndPopularity() {
        UserProductFeature feature = UserProductFeature.builder()
                .product(product)
                .viewCount(5)       // 5 * 1 = 5
                .cartCount(2)       // 2 * 3 = 6
                .purchaseCount(1)   // 1 * 10 = 10
                .wishlistCount(1)   // 1 * 2 = 2
                .lastInteraction(LocalDateTime.now()) // recency = +5
                .build();

        ProductStatistic stat = ProductStatistic.builder()
                .productId(10L)
                .viewCount(10L)
                .cartCount(5L)
                .purchaseCount(2L)
                .build();

        when(productStatisticRepository.findById(10L)).thenReturn(Optional.of(stat));
        when(productStatisticRepository.findAll()).thenReturn(Collections.singletonList(stat));

        double score = scoringStrategy.score(feature);

        // Expected base: 5 + 6 + 10 + 2 = 23
        // Recency: 5.0
        // Normalized popularity: (10*1 + 5*3 + 2*10) / (45) = 1.0 * 0.5 = 0.5
        // Expected total approx: 28.5
        assertTrue(score >= 28.0 && score <= 29.0, "Score should be close to 28.5, got: " + score);
    }
}

package com.PriceIQ.PriceIQ.service;

import com.PriceIQ.PriceIQ.dto.response.ProductResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.ProductAttribute;
import com.PriceIQ.PriceIQ.entity.ProductStatistic;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.entity.UserRecommendation;
import com.PriceIQ.PriceIQ.repository.OrderItemRepository;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.ProductStatisticRepository;
import com.PriceIQ.PriceIQ.repository.UserRecommendationRepository;
import com.PriceIQ.PriceIQ.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private UserRecommendationRepository recommendationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStatisticRepository productStatisticRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Product sourceProduct;
    private Product candidateBrandMatch;
    private Product candidateOtherBrand;

    @BeforeEach
    void setUp() {
        sourceProduct = Product.builder()
                .id(1L)
                .name("Galaxy S24")
                .brand("Samsung")
                .category("Electronics")
                .currentPrice(BigDecimal.valueOf(80000))
                .stockQuantity(15)
                .active(true)
                .averageRating(4.7)
                .attributes(Arrays.asList(
                        ProductAttribute.builder().attributeName("color").attributeValue("black").build(),
                        ProductAttribute.builder().attributeName("storage").attributeValue("256GB").build()
                ))
                .build();

        candidateBrandMatch = Product.builder()
                .id(2L)
                .name("Galaxy S24 Ultra")
                .brand("Samsung")
                .category("Electronics")
                .currentPrice(BigDecimal.valueOf(85000))
                .stockQuantity(10)
                .active(true)
                .averageRating(4.9)
                .attributes(Arrays.asList(
                        ProductAttribute.builder().attributeName("color").attributeValue("black").build(),
                        ProductAttribute.builder().attributeName("storage").attributeValue("512GB").build()
                ))
                .build();

        candidateOtherBrand = Product.builder()
                .id(3L)
                .name("Pixel 8 Pro")
                .brand("Google")
                .category("Electronics")
                .currentPrice(BigDecimal.valueOf(75000))
                .stockQuantity(8)
                .active(true)
                .averageRating(4.5)
                .attributes(Collections.singletonList(
                        ProductAttribute.builder().attributeName("color").attributeValue("blue").build()
                ))
                .build();
    }

    @Test
    void getFeedForUser_WhenRecommendationsExist_ShouldReturnRankedProducts() {
        User user = User.builder().id(100L).build();
        UserRecommendation rec = UserRecommendation.builder()
                .user(user)
                .product(candidateBrandMatch)
                .rank(1)
                .score(0.95)
                .build();

        when(recommendationRepository.findByUserIdOrderByRankAsc(100L))
                .thenReturn(Collections.singletonList(rec));

        List<ProductResponse> feed = recommendationService.getFeedForUser(100L);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals(2L, feed.get(0).getId());
        assertEquals("Galaxy S24 Ultra", feed.get(0).getName());
    }

    @Test
    void getFeedForUser_WhenNoRecommendationsExist_ShouldFallbackToTrending() {
        when(recommendationRepository.findByUserIdOrderByRankAsc(100L))
                .thenReturn(Collections.emptyList());

        ProductStatistic stat = ProductStatistic.builder()
                .productId(2L)
                .viewCount(100L)
                .cartCount(20L)
                .purchaseCount(5L)
                .build();

        when(productStatisticRepository.findAll()).thenReturn(Collections.singletonList(stat));
        when(productRepository.findAllById(any())).thenReturn(Collections.singletonList(candidateBrandMatch));

        List<ProductResponse> feed = recommendationService.getFeedForUser(100L);

        assertNotNull(feed);
        assertFalse(feed.isEmpty());
        assertEquals(2L, feed.get(0).getId());
    }

    @Test
    void getSimilarProducts_ShouldPrioritizeSameBrandAndSharedAttributes() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sourceProduct));
        when(productRepository.findByCategoryAndActiveTrueAndStockQuantityGreaterThan(eq("Electronics"), eq(0)))
                .thenReturn(Arrays.asList(sourceProduct, candidateOtherBrand, candidateBrandMatch));

        List<ProductResponse> similar = recommendationService.getSimilarProducts(1L, 5);

        assertNotNull(similar);
        assertEquals(2, similar.size());
        // Candidate 2 (Samsung) ranks higher than candidate 3 (Google) due to brand match + shared attribute name
        assertEquals(2L, similar.get(0).getId());
        assertEquals(3L, similar.get(1).getId());
    }

    @Test
    void getFrequentlyBoughtTogether_WhenCoPurchasedFound_ShouldReturnThem() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findCoPurchasedProductIds(eq(1L), anyInt()))
                .thenReturn(Collections.singletonList(2L));
        when(productRepository.findAllById(any())).thenReturn(Collections.singletonList(candidateBrandMatch));

        List<ProductResponse> fbt = recommendationService.getFrequentlyBoughtTogether(1L, 4);

        assertNotNull(fbt);
        assertEquals(1, fbt.size());
        assertEquals(2L, fbt.get(0).getId());
    }

    @Test
    void getTrendingProducts_ShouldRankByWeightedFormula() {
        ProductStatistic statLow = ProductStatistic.builder()
                .productId(2L)
                .viewCount(10L)
                .cartCount(0L)
                .purchaseCount(0L)
                .build();

        ProductStatistic statHigh = ProductStatistic.builder()
                .productId(3L)
                .viewCount(10L)
                .cartCount(5L)
                .purchaseCount(2L)
                .build();

        when(productStatisticRepository.findAll()).thenReturn(Arrays.asList(statLow, statHigh));
        when(productRepository.findAllById(any())).thenReturn(Arrays.asList(candidateBrandMatch, candidateOtherBrand));

        List<ProductResponse> trending = recommendationService.getTrendingProducts(2);

        assertNotNull(trending);
        assertEquals(2, trending.size());
        // Product 3 has higher score (10*1 + 5*3 + 2*10 = 45 vs 10)
        assertEquals(3L, trending.get(0).getId());
        assertEquals(2L, trending.get(1).getId());
    }
}

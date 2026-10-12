package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
/**
 * ML Feature store: one row per (user, product) pair.
 * Updated via UPSERT by the recommendation Kafka consumer.
 * Encapsulates the raw interaction counts used for scoring.
 */
@Entity
@Table(name = "user_product_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProductFeature {

    @EmbeddedId
    private UserProductFeatureId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer cartCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer purchaseCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer wishlistCount = 0;

    @Column(nullable = false)
    private LocalDateTime lastInteraction;
}

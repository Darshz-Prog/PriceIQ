package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pre-aggregated product-level statistics.
 * Updated asynchronously by the analytics Kafka consumer.
 * productId is the PK (same as products.id); no auto-generation needed.
 */
@Entity
@Table(name = "product_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatistic {

    /** Same ID as the corresponding Product — no auto-generation. */
    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long cartCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long purchaseCount = 0L;
}

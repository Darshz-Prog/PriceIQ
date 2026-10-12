package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Stores pre-computed top-N product recommendations per user.
 * Written by the recommendation Kafka consumer after each scoring run.
 * Read by GET /api/v1/recommendations/feed without any Kafka involvement.
 */
@Entity
@Table(
    name = "user_recommendations",
    indexes = {
        @Index(name = "idx_user_rec_user_id", columnList = "user_id"),
        @Index(name = "idx_user_rec_user_rank", columnList = "user_id, rank")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Computed relevance score from the scoring strategy */
    @Column(nullable = false)
    private Double score;

    /** Rank 1 = most recommended */
    @Column(nullable = false)
    private Integer rank;

    @CreationTimestamp
    private LocalDateTime generatedAt;
}

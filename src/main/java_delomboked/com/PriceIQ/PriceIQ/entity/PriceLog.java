package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal oldPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal newPrice;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column
    private String changedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
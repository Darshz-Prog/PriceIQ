package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_weights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double demandWeight;

    @Column(nullable = false)
    private Double stockWeight;

    @Column(nullable = false)
    private Double competitorWeight;

    @Column(nullable = false)
    private Double loyaltyWeight;

    @Column(nullable = false)
    private Double seasonalWeight;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
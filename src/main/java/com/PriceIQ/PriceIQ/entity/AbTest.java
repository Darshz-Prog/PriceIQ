package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ab_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //! NOT COMPLETED
    private Long id;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false)
    private String variantA;

    @Column(nullable = false)
    private String variantB;

    @Column(nullable = false)
    private Double conversionRateA = 0.0;

    @Column(nullable = false)
    private Double conversionRateB = 0.0;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
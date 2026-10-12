package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Composite key for UserProductFeature.
 * Represents the (user_id, product_id) pair uniquely.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserProductFeatureId implements java.io.Serializable {
    private Long userId;
    private Long productId;
}

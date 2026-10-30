package com.PriceIQ.PriceIQ.repository;

import com.PriceIQ.PriceIQ.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    /**
     * Co-purchase query: returns IDs of products that appear in the same orders
     * as {@code productId}, ordered by co-occurrence count descending.
     * Excludes the source product itself.
     *
     * Example: if iPhone and AirPods appear together in 50 orders, AirPods gets
     * a co-count of 50 and ranks at the top of the FBT list.
     */
    @Query("""
            SELECT oi2.product.id
            FROM OrderItem oi1
            JOIN OrderItem oi2 ON oi1.order.id = oi2.order.id
            WHERE oi1.product.id = :productId
              AND oi2.product.id <> :productId
            GROUP BY oi2.product.id
            ORDER BY COUNT(oi2.product.id) DESC
            """)
    List<Long> findCoPurchasedProductIds(
            @Param("productId") Long productId,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Convenience overload that accepts a plain int limit using a LIMIT clause.
     */
    default List<Long> findCoPurchasedProductIds(Long productId, int limit) {
        return findCoPurchasedProductIds(
                productId,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }
}
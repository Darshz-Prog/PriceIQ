package com.PriceIQ.PriceIQ.specification;

import com.PriceIQ.PriceIQ.entity.Product;
import org.springframework.data.jpa.domain.Specification;


public class ProductSpecification {

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) ->
                category == null || category.isBlank()
                        ? null
                        : cb.equal(
                                cb.lower(root.get("category")),
                                category.toLowerCase()
                        );
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null || brand.isBlank()
                        ? null
                        : cb.equal(
                                cb.lower(root.get("brand")),
                                brand.toLowerCase()
                        );
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                                root.get("currentPrice"),
                                minPrice
                        );
    }

    public static Specification<Product> priceLessThanOrEqualTo(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? null
                        : cb.lessThanOrEqualTo(
                                root.get("currentPrice"),
                                maxPrice
                        );
    }
}
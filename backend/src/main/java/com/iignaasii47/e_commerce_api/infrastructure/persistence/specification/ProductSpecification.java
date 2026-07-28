package com.iignaasii47.e_commerce_api.infrastructure.persistence.specification;

import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<ProductEntity> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<ProductEntity> matchesSearch(String query) {
        return (root, cq, cb) -> {
            String pattern = "%" + query.toLowerCase() + "%";
            Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
            Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
            return cb.or(nameMatch, descMatch);
        };
    }

}

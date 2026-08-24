package com.example.springboot_2.specification;

import com.example.springboot_2.model.Product.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> hasName(String name){
        return (root,query,cb)->
                name == null ? null : cb.like(cb.lower(root.get("name")),"%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice){
        return (root,query,cb)->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"),minPrice);
    }
    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice){
        return (root,query,cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"),maxPrice);
    }
}

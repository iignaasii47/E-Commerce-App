package com.iignaasii47.e_commerce_api.util;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

public final class TestFixtures {

    public static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    public static Product aKeyboardProduct() {
        return Product.builder()
                .id(1L).name("Keyboard").description("A keyboard")
                .price(new BigDecimal("149.99")).category("peripherals").imageUrl("http://img.url")
                .stock(10).rating(4.5).build();
    }

    public static ProductEntity aKeyboardProductEntity() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Keyboard");
        entity.setDescription("A keyboard");
        entity.setPrice(new BigDecimal("149.99"));
        entity.setCategory("peripherals");
        entity.setImageUrl("http://img.url");
        entity.setStock(10);
        entity.setRating(4.5);
        return entity;
    }

    public static User aJohnUser() {
        return new User(1L, "john", "john@example.com", "secret", FIXED_TIME);
    }

    public static UserEntity aJohnUserEntity() {
        return new UserEntity(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
    }

    private TestFixtures() {
    }
}

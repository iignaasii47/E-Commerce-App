package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemEntityTest {

    @Test
    void shouldCreateWithConstructor() {
        CartItemEntity entity = new CartItemEntity(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getProductId()).isEqualTo(5L);
        assertThat(entity.getProductName()).isEqualTo("Keyboard");
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldSetAndGetId() {
        CartItemEntity entity = new CartItemEntity();
        entity.setId(1L);
        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void shouldSetAndGetUserId() {
        CartItemEntity entity = new CartItemEntity();
        entity.setUserId(10L);
        assertThat(entity.getUserId()).isEqualTo(10L);
    }

    @Test
    void shouldSetAndGetProductId() {
        CartItemEntity entity = new CartItemEntity();
        entity.setProductId(5L);
        assertThat(entity.getProductId()).isEqualTo(5L);
    }

    @Test
    void shouldSetAndGetProductName() {
        CartItemEntity entity = new CartItemEntity();
        entity.setProductName("Mouse");
        assertThat(entity.getProductName()).isEqualTo("Mouse");
    }

    @Test
    void shouldSetAndGetUnitPrice() {
        CartItemEntity entity = new CartItemEntity();
        entity.setUnitPrice(new BigDecimal("79.99"));
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("79.99");
    }

    @Test
    void shouldSetAndGetQuantity() {
        CartItemEntity entity = new CartItemEntity();
        entity.setQuantity(3);
        assertThat(entity.getQuantity()).isEqualTo(3);
    }

}

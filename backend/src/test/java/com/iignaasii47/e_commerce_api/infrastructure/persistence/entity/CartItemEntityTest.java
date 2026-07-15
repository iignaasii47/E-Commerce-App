package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemEntityTest {

    @Test
    void shouldCreateWithConstructor() {
        CartItemEntity entity = new CartItemEntity(1L, 10L, 5L, "Keyboard", new java.math.BigDecimal("149.99"), 2);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getProductId()).isEqualTo(5L);
        assertThat(entity.getProductName()).isEqualTo("Keyboard");
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getQuantity()).isEqualTo(2);
    }

}

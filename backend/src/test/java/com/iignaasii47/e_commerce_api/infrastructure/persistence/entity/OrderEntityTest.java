package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityTest {

    @Test
    void shouldSetFields() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setUserId(10L);
        entity.setStatus("CONFIRMED");
        entity.setTotal(new java.math.BigDecimal("299.98"));
        entity.setShippingAddress("123 Main St");
        entity.setShippingCity("Springfield");
        entity.setShippingZip("12345");
        entity.setCreatedAt(LocalDateTime.of(2026, 7, 21, 12, 0));

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getStatus()).isEqualTo("CONFIRMED");
        assertThat(entity.getTotal()).isEqualByComparingTo("299.98");
        assertThat(entity.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(entity.getShippingCity()).isEqualTo("Springfield");
        assertThat(entity.getShippingZip()).isEqualTo("12345");
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        OrderEntity entity = new OrderEntity();
        entity.onCreate();

        assertThat(entity.getCreatedAt()).isNotNull();
    }

}

package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void shouldHaveThreeValues() {
        assertThat(OrderStatus.values()).containsExactly(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
    }

    @Test
    void shouldResolveByName() {
        assertThat(OrderStatus.valueOf("PENDING")).isEqualTo(OrderStatus.PENDING);
        assertThat(OrderStatus.valueOf("CONFIRMED")).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(OrderStatus.valueOf("CANCELLED")).isEqualTo(OrderStatus.CANCELLED);
    }

}

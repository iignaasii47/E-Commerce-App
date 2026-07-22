package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    void shouldStoreAllFields() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getProductId()).isEqualTo(5L);
        assertThat(item.getProductName()).isEqualTo("Keyboard");
        assertThat(item.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldCalculateSubtotal() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        assertThat(item.getSubtotal()).isEqualByComparingTo("299.98");
    }

    @Test
    void shouldEqualWhenSameId() {
        OrderItem item1 = new OrderItem(1L, 5L, "A", BigDecimal.ONE, 1);
        OrderItem item2 = new OrderItem(1L, 10L, "B", BigDecimal.TEN, 3);

        assertThat(item1).isEqualTo(item2).hasSameHashCodeAs(item2);
    }

    @Test
    void shouldNotEqualWhenDifferentId() {
        OrderItem item1 = new OrderItem(1L, 5L, "A", BigDecimal.ONE, 1);
        OrderItem item2 = new OrderItem(2L, 5L, "A", BigDecimal.ONE, 1);

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldNotEqualNull() {
        OrderItem item = new OrderItem(1L, 5L, "A", BigDecimal.ONE, 1);

        assertThat(item).isNotEqualTo(null);
    }

}

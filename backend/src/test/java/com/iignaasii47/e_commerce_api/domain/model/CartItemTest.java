package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CartItemTest {

    @Test
    void shouldStoreAllFields() {
        CartItem item = new CartItem(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getUserId()).isEqualTo(10L);
        assertThat(item.getProductId()).isEqualTo(5L);
        assertThat(item.getProductName()).isEqualTo("Keyboard");
        assertThat(item.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldCalculateSubtotal() {
        CartItem item = new CartItem(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        assertThat(item.getSubtotal()).isEqualByComparingTo("299.98");
    }

    @Test
    void shouldEqualWhenSameId() {
        CartItem item1 = new CartItem(1L, 10L, 5L, "A", BigDecimal.ONE, 1);
        CartItem item2 = new CartItem(1L, 20L, 30L, "B", BigDecimal.TEN, 5);

        assertThat(item1).isEqualTo(item2).hasSameHashCodeAs(item2);
    }

    @Test
    void shouldNotEqualWhenDifferentId() {
        CartItem item1 = new CartItem(1L, 10L, 5L, "A", BigDecimal.ONE, 1);
        CartItem item2 = new CartItem(2L, 10L, 5L, "A", BigDecimal.ONE, 1);

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldNotEqualNull() {
        CartItem item = new CartItem(1L, 10L, 5L, "A", BigDecimal.ONE, 1);

        assertThat(item).isNotEqualTo(null);
    }

}

package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void shouldStoreAllFields() {
        Product product = new Product(1L, "Keyboard", "A mechanical keyboard",
                new BigDecimal("149.99"), "peripherals", "http://img.url", 10, 4.5);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Keyboard");
        assertThat(product.getDescription()).isEqualTo("A mechanical keyboard");
        assertThat(product.getPrice()).isEqualByComparingTo("149.99");
        assertThat(product.getCategory()).isEqualTo("peripherals");
        assertThat(product.getImageUrl()).isEqualTo("http://img.url");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getRating()).isEqualTo(4.5);
    }

    @Test
    void shouldEqualWhenSameId() {
        Product p1 = new Product(1L, "A", "desc", BigDecimal.ONE, "cat", "img", 1, 1.0);
        Product p2 = new Product(1L, "B", "other", BigDecimal.TEN, "cat2", "img2", 2, 2.0);

        assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
    }

    @Test
    void shouldNotEqualWhenDifferentId() {
        Product p1 = new Product(1L, "A", "desc", BigDecimal.ONE, "cat", "img", 1, 1.0);
        Product p2 = new Product(2L, "A", "desc", BigDecimal.ONE, "cat", "img", 1, 1.0);

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void shouldNotEqualNull() {
        Product p1 = new Product(1L, "A", "desc", BigDecimal.ONE, "cat", "img", 1, 1.0);

        assertThat(p1).isNotEqualTo(null);
    }

    @Test
    void shouldNotEqualDifferentType() {
        Product p1 = new Product(1L, "A", "desc", BigDecimal.ONE, "cat", "img", 1, 1.0);

        assertThat(p1).isNotEqualTo("not a product");
    }

}

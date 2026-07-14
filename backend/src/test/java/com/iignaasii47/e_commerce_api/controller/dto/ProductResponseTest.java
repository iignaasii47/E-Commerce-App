package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseTest {

    @Test
    void shouldMapProductToResponse() {
        Product product = new Product(1L, "Keyboard", "A keyboard",
                new BigDecimal("149.99"), "peripherals", "http://img.url", 10, 4.5);

        ProductResponse response = ProductResponse.from(product);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Keyboard");
        assertThat(response.getDescription()).isEqualTo("A keyboard");
        assertThat(response.getPrice()).isEqualByComparingTo("149.99");
        assertThat(response.getCategory()).isEqualTo("peripherals");
        assertThat(response.getImage()).isEqualTo("http://img.url");
        assertThat(response.getStock()).isEqualTo(10);
        assertThat(response.getRating()).isEqualTo(4.5);
    }

    @Test
    void shouldMapImageUrlToImageField() {
        Product product = new Product(1L, "Test", "desc",
                BigDecimal.ONE, "cat", "https://example.com/img.png", 0, 0);

        ProductResponse response = ProductResponse.from(product);

        assertThat(response.getImage()).isEqualTo("https://example.com/img.png");
    }

}

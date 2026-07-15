package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void shouldStoreAllFields() {
        Product product = Product.builder()
                .id(1L)
                .name("Keyboard")
                .description("A mechanical keyboard")
                .price(new BigDecimal("149.99"))
                .category("peripherals")
                .imageUrl("http://img.url")
                .stock(10)
                .rating(4.5)
                .build();

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
        Product p1 = Product.builder()
                .id(1L).name("A").description("desc")
                .price(BigDecimal.ONE).category("cat").imageUrl("img")
                .stock(1).rating(1.0).build();
        Product p2 = Product.builder()
                .id(1L).name("B").description("other")
                .price(BigDecimal.TEN).category("cat2").imageUrl("img2")
                .stock(2).rating(2.0).build();

        assertThat(p1).isEqualTo(p2).hasSameHashCodeAs(p2);
    }

    @Test
    void shouldNotEqualWhenDifferentId() {
        Product p1 = Product.builder()
                .id(1L).name("A").description("desc")
                .price(BigDecimal.ONE).category("cat").imageUrl("img")
                .stock(1).rating(1.0).build();
        Product p2 = Product.builder()
                .id(2L).name("A").description("desc")
                .price(BigDecimal.ONE).category("cat").imageUrl("img")
                .stock(1).rating(1.0).build();

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void shouldNotEqualNull() {
        Product p1 = Product.builder()
                .id(1L).name("A").description("desc")
                .price(BigDecimal.ONE).category("cat").imageUrl("img")
                .stock(1).rating(1.0).build();

        assertThat(p1).isNotEqualTo(null);
    }

    @Test
    void shouldNotEqualDifferentType() {
        Product p1 = Product.builder()
                .id(1L).name("A").description("desc")
                .price(BigDecimal.ONE).category("cat").imageUrl("img")
                .stock(1).rating(1.0).build();

        assertThat(p1).isNotEqualTo("not a product");
    }

}

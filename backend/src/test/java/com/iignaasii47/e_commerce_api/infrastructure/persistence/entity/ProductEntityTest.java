package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEntityTest {

    @Test
    void shouldCreateWithConstructor() {
        ProductEntity entity = new ProductEntity(1L, "Keyboard", "desc",
                new BigDecimal("149.99"), "peripherals", "img", 10, 4.5);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Keyboard");
        assertThat(entity.getDescription()).isEqualTo("desc");
        assertThat(entity.getPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getCategory()).isEqualTo("peripherals");
        assertThat(entity.getImageUrl()).isEqualTo("img");
        assertThat(entity.getStock()).isEqualTo(10);
        assertThat(entity.getRating()).isEqualTo(4.5);
    }

    @Test
    void shouldSetAndGetId() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void shouldSetAndGetName() {
        ProductEntity entity = new ProductEntity();
        entity.setName("Mouse");
        assertThat(entity.getName()).isEqualTo("Mouse");
    }

    @Test
    void shouldSetAndGetDescription() {
        ProductEntity entity = new ProductEntity();
        entity.setDescription("A wireless mouse");
        assertThat(entity.getDescription()).isEqualTo("A wireless mouse");
    }

    @Test
    void shouldSetAndGetPrice() {
        ProductEntity entity = new ProductEntity();
        entity.setPrice(new BigDecimal("79.99"));
        assertThat(entity.getPrice()).isEqualByComparingTo("79.99");
    }

    @Test
    void shouldSetAndGetCategory() {
        ProductEntity entity = new ProductEntity();
        entity.setCategory("peripherals");
        assertThat(entity.getCategory()).isEqualTo("peripherals");
    }

    @Test
    void shouldSetAndGetImageUrl() {
        ProductEntity entity = new ProductEntity();
        entity.setImageUrl("http://img.url");
        assertThat(entity.getImageUrl()).isEqualTo("http://img.url");
    }

    @Test
    void shouldSetAndGetStock() {
        ProductEntity entity = new ProductEntity();
        entity.setStock(5);
        assertThat(entity.getStock()).isEqualTo(5);
    }

    @Test
    void shouldSetAndGetRating() {
        ProductEntity entity = new ProductEntity();
        entity.setRating(4.2);
        assertThat(entity.getRating()).isEqualTo(4.2);
    }

}

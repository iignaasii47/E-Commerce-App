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

}

package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    @Test
    void shouldMapEntityToDomain() {
        ProductEntity entity = new ProductEntity(1L, "Keyboard", "A keyboard",
                new BigDecimal("149.99"), "peripherals", "http://img.url", 10, 4.5);

        Product product = ProductMapper.toDomain(entity);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Keyboard");
        assertThat(product.getDescription()).isEqualTo("A keyboard");
        assertThat(product.getPrice()).isEqualByComparingTo("149.99");
        assertThat(product.getCategory()).isEqualTo("peripherals");
        assertThat(product.getImageUrl()).isEqualTo("http://img.url");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.getRating()).isEqualTo(4.5);
    }

    @Test
    void shouldMapDomainToEntity() {
        Product product = new Product(1L, "Keyboard", "A keyboard",
                new BigDecimal("149.99"), "peripherals", "http://img.url", 10, 4.5);

        ProductEntity entity = ProductMapper.toEntity(product);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Keyboard");
        assertThat(entity.getDescription()).isEqualTo("A keyboard");
        assertThat(entity.getPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getCategory()).isEqualTo("peripherals");
        assertThat(entity.getImageUrl()).isEqualTo("http://img.url");
        assertThat(entity.getStock()).isEqualTo(10);
        assertThat(entity.getRating()).isEqualTo(4.5);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(ProductMapper.toDomain(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(ProductMapper.toEntity(null)).isNull();
    }

}

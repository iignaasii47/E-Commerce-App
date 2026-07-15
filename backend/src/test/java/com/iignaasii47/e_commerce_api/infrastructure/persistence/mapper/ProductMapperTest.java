package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;
import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    @Test
    void shouldMapEntityToDomain() {
        ProductEntity entity = TestFixtures.aKeyboardProductEntity();

        Product product = ProductMapper.toDomain(entity);

        assertThat(product).extracting(Product::getId, Product::getName, Product::getDescription,
                        Product::getPrice, Product::getCategory, Product::getImageUrl,
                        Product::getStock, Product::getRating)
                .containsExactly(entity.getId(), entity.getName(), entity.getDescription(),
                        entity.getPrice(), entity.getCategory(), entity.getImageUrl(),
                        entity.getStock(), entity.getRating());
    }

    @Test
    void shouldMapDomainToEntity() {
        Product product = TestFixtures.aKeyboardProduct();

        ProductEntity entity = ProductMapper.toEntity(product);

        assertThat(entity).extracting(ProductEntity::getId, ProductEntity::getName,
                        ProductEntity::getDescription, ProductEntity::getPrice,
                        ProductEntity::getCategory, ProductEntity::getImageUrl,
                        ProductEntity::getStock, ProductEntity::getRating)
                .containsExactly(product.getId(), product.getName(), product.getDescription(),
                        product.getPrice(), product.getCategory(), product.getImageUrl(),
                        product.getStock(), product.getRating());
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

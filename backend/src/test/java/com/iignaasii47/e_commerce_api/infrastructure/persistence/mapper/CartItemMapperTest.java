package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.CartItemEntity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemMapperTest {

    @Test
    void shouldMapEntityToDomain() {
        CartItemEntity entity = new CartItemEntity(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        CartItem item = CartItemMapper.toDomain(entity);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getUserId()).isEqualTo(10L);
        assertThat(item.getProductId()).isEqualTo(5L);
        assertThat(item.getProductName()).isEqualTo("Keyboard");
        assertThat(item.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldMapDomainToEntity() {
        CartItem item = new CartItem(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        CartItemEntity entity = CartItemMapper.toEntity(item);

        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getProductId()).isEqualTo(5L);
        assertThat(entity.getProductName()).isEqualTo("Keyboard");
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(CartItemMapper.toDomain(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(CartItemMapper.toEntity(null)).isNull();
    }

}

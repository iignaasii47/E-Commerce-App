package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.OrderItem;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemResponseTest {

    @Test
    void shouldMapFromDomain() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        OrderItemResponse response = OrderItemResponse.from(item);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(5L);
        assertThat(response.getProductName()).isEqualTo("Keyboard");
        assertThat(response.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualByComparingTo("299.98");
    }

}

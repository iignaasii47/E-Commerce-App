package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderResponseTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, Month.JULY, 21, 12, 0);

    @Test
    void shouldMapFromDomain() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        Order order = Order.builder()
                .id(1L).userId(10L).status(OrderStatus.CONFIRMED)
                .items(List.of(item)).total(new BigDecimal("299.98"))
                .shippingAddress("123 Main St").shippingCity("Springfield").shippingZip("12345")
                .createdAt(NOW).build();

        OrderResponse response = OrderResponse.from(order);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getTotal()).isEqualByComparingTo("299.98");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductName()).isEqualTo("Keyboard");
        assertThat(response.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(response.getShippingCity()).isEqualTo("Springfield");
        assertThat(response.getShippingZip()).isEqualTo("12345");
        assertThat(response.getCreatedAt()).isEqualTo(NOW);
    }

}

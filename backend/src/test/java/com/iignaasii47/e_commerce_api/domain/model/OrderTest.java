package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, Month.JULY, 21, 12, 0);

    @Test
    void shouldStoreAllFieldsViaBuilder() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 1);
        Order order = Order.builder()
                .id(1L)
                .userId(10L)
                .status(OrderStatus.CONFIRMED)
                .items(List.of(item))
                .total(new BigDecimal("149.99"))
                .shippingAddress("123 Main St")
                .shippingCity("Springfield")
                .shippingZip("12345")
                .createdAt(NOW)
                .build();

        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getUserId()).isEqualTo(10L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotal()).isEqualByComparingTo("149.99");
        assertThat(order.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(order.getShippingCity()).isEqualTo("Springfield");
        assertThat(order.getShippingZip()).isEqualTo("12345");
        assertThat(order.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void shouldEqualWhenSameId() {
        OrderItem item = new OrderItem(1L, 5L, "A", BigDecimal.ONE, 1);
        Order order1 = Order.builder().id(1L).userId(10L).status(OrderStatus.PENDING)
                .items(List.of(item)).total(BigDecimal.ONE)
                .shippingAddress("A").shippingCity("B").shippingZip("C").createdAt(NOW).build();
        Order order2 = Order.builder().id(1L).userId(20L).status(OrderStatus.CONFIRMED)
                .items(List.of()).total(BigDecimal.TEN)
                .shippingAddress("X").shippingCity("Y").shippingZip("Z").createdAt(NOW).build();

        assertThat(order1).isEqualTo(order2).hasSameHashCodeAs(order2);
    }

    @Test
    void shouldNotEqualWhenDifferentId() {
        OrderItem item = new OrderItem(1L, 5L, "A", BigDecimal.ONE, 1);
        Order order1 = Order.builder().id(1L).userId(10L).status(OrderStatus.PENDING)
                .items(List.of(item)).total(BigDecimal.ONE)
                .shippingAddress("A").shippingCity("B").shippingZip("C").createdAt(NOW).build();
        Order order2 = Order.builder().id(2L).userId(10L).status(OrderStatus.PENDING)
                .items(List.of(item)).total(BigDecimal.ONE)
                .shippingAddress("A").shippingCity("B").shippingZip("C").createdAt(NOW).build();

        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void shouldNotEqualNull() {
        OrderItem item = new OrderItem(1L, 5L, "A", BigDecimal.ONE, 1);
        Order order = Order.builder().id(1L).userId(10L).status(OrderStatus.PENDING)
                .items(List.of(item)).total(BigDecimal.ONE)
                .shippingAddress("A").shippingCity("B").shippingZip("C").createdAt(NOW).build();

        assertThat(order).isNotEqualTo(null);
    }

}

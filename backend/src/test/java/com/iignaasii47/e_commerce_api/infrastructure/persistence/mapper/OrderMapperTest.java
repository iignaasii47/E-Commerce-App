package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderItemEntity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, Month.JULY, 21, 12, 0);

    @Test
    void shouldMapEntityToDomain() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setUserId(10L);
        entity.setStatus("CONFIRMED");
        entity.setTotal(new BigDecimal("299.98"));
        entity.setShippingAddress("123 Main St");
        entity.setShippingCity("Springfield");
        entity.setShippingZip("12345");
        entity.setCreatedAt(NOW);

        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setId(1L);
        itemEntity.setOrderId(1L);
        itemEntity.setProductId(5L);
        itemEntity.setProductName("Keyboard");
        itemEntity.setUnitPrice(new BigDecimal("149.99"));
        itemEntity.setQuantity(2);

        Order order = OrderMapper.toDomain(entity, List.of(itemEntity));

        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getUserId()).isEqualTo(10L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getTotal()).isEqualByComparingTo("299.98");
        assertThat(order.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(order.getShippingCity()).isEqualTo("Springfield");
        assertThat(order.getShippingZip()).isEqualTo("12345");
        assertThat(order.getCreatedAt()).isEqualTo(NOW);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getProductName()).isEqualTo("Keyboard");
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(OrderMapper.toDomain(null, List.of())).isNull();
    }

    @Test
    void shouldHandleNullItemList() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setUserId(10L);
        entity.setStatus("PENDING");
        entity.setTotal(BigDecimal.ZERO);
        entity.setShippingAddress("A");
        entity.setShippingCity("B");
        entity.setShippingZip("C");
        entity.setCreatedAt(NOW);

        Order order = OrderMapper.toDomain(entity, null);

        assertThat(order.getItems()).isEmpty();
    }

    @Test
    void shouldMapDomainToEntity() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        Order order = Order.builder()
                .id(1L).userId(10L).status(OrderStatus.CONFIRMED)
                .items(List.of(item)).total(new BigDecimal("299.98"))
                .shippingAddress("123 Main St").shippingCity("Springfield").shippingZip("12345")
                .createdAt(NOW).build();

        OrderEntity entity = OrderMapper.toEntity(order);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getStatus()).isEqualTo("CONFIRMED");
        assertThat(entity.getTotal()).isEqualByComparingTo("299.98");
        assertThat(entity.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(entity.getShippingCity()).isEqualTo("Springfield");
        assertThat(entity.getShippingZip()).isEqualTo("12345");
    }

    @Test
    void shouldReturnNullEntityWhenOrderIsNull() {
        assertThat(OrderMapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityItemToDomain() {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setProductId(5L);
        entity.setProductName("Keyboard");
        entity.setUnitPrice(new BigDecimal("149.99"));
        entity.setQuantity(2);

        OrderItem item = OrderMapper.toDomainItem(entity);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getProductId()).isEqualTo(5L);
        assertThat(item.getProductName()).isEqualTo("Keyboard");
        assertThat(item.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldReturnNullWhenEntityItemIsNull() {
        assertThat(OrderMapper.toDomainItem(null)).isNull();
    }

    @Test
    void shouldMapDomainItemToEntity() {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        OrderItemEntity entity = OrderMapper.toEntityItem(item, 1L);

        assertThat(entity.getOrderId()).isEqualTo(1L);
        assertThat(entity.getProductId()).isEqualTo(5L);
        assertThat(entity.getProductName()).isEqualTo("Keyboard");
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("149.99");
        assertThat(entity.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldReturnNullWhenDomainItemIsNull() {
        assertThat(OrderMapper.toEntityItem(null, 1L)).isNull();
    }

}

package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderItemEntity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, Month.JULY, 21, 12, 0);

    @Mock
    private JpaOrderRepository jpaOrderRepository;

    @Mock
    private JpaOrderItemRepository jpaOrderItemRepository;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @Test
    void shouldCreateOrder() {
        OrderEntity savedEntity = new OrderEntity();
        savedEntity.setId(1L);
        savedEntity.setUserId(10L);
        savedEntity.setStatus("CONFIRMED");
        savedEntity.setTotal(new BigDecimal("299.98"));
        savedEntity.setShippingAddress("123 Main St");
        savedEntity.setShippingCity("Springfield");
        savedEntity.setShippingZip("12345");
        savedEntity.setCreatedAt(NOW);

        when(jpaOrderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);

        OrderItem item = new OrderItem(null, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        Order result = orderRepository.createOrder(10L, OrderStatus.CONFIRMED,
                List.of(item), new BigDecimal("299.98"),
                "123 Main St", "Springfield", "12345");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(result.getTotal()).isEqualByComparingTo("299.98");
        assertThat(result.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(result.getShippingCity()).isEqualTo("Springfield");
        assertThat(result.getShippingZip()).isEqualTo("12345");
        assertThat(result.getItems()).hasSize(1);

        verify(jpaOrderItemRepository).saveAll(any());
    }

    @Test
    void shouldFindById() {
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

        when(jpaOrderRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(jpaOrderItemRepository.findByOrderId(1L)).thenReturn(List.of(itemEntity));

        Optional<Order> result = orderRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getItems()).hasSize(1);
        assertThat(result.get().getItems().get(0).getProductName()).isEqualTo("Keyboard");
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        when(jpaOrderRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Order> result = orderRepository.findById(99L);

        assertThat(result).isEmpty();
    }

}

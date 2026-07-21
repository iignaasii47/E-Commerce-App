package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;
import com.iignaasii47.e_commerce_api.domain.port.out.OrderRepository;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderItemEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper.OrderMapper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final JpaOrderItemRepository jpaOrderItemRepository;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository, JpaOrderItemRepository jpaOrderItemRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.jpaOrderItemRepository = jpaOrderItemRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderStatus status, List<OrderItem> items, BigDecimal total,
                             String shippingAddress, String shippingCity, String shippingZip) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setStatus(status.name());
        orderEntity.setTotal(total);
        orderEntity.setShippingAddress(shippingAddress);
        orderEntity.setShippingCity(shippingCity);
        orderEntity.setShippingZip(shippingZip);
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        List<OrderItemEntity> itemEntities = items.stream()
                .map(item -> OrderMapper.toEntityItem(item, savedOrder.getId()))
                .toList();
        jpaOrderItemRepository.saveAll(itemEntities);

        return OrderMapper.toDomain(savedOrder, itemEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long orderId) {
        return jpaOrderRepository.findById(orderId)
                .map(entity -> {
                    List<OrderItemEntity> items = jpaOrderItemRepository.findByOrderId(entity.getId());
                    return OrderMapper.toDomain(entity, items);
                });
    }

}

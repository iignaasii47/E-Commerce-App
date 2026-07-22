package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.OrderItemEntity;

import java.util.Collections;
import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static Order toDomain(OrderEntity entity, List<OrderItemEntity> itemEntities) {
        if (entity == null) {
            return null;
        }
        List<OrderItem> items = itemEntities == null
                ? Collections.emptyList()
                : itemEntities.stream().map(OrderMapper::toDomainItem).toList();
        return Order.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .status(OrderStatus.valueOf(entity.getStatus()))
                .items(items)
                .total(entity.getTotal())
                .shippingAddress(entity.getShippingAddress())
                .shippingCity(entity.getShippingCity())
                .shippingZip(entity.getShippingZip())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setStatus(order.getStatus().name());
        entity.setTotal(order.getTotal());
        entity.setShippingAddress(order.getShippingAddress());
        entity.setShippingCity(order.getShippingCity());
        entity.setShippingZip(order.getShippingZip());
        return entity;
    }

    public static OrderItem toDomainItem(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getProductName(),
                entity.getUnitPrice(),
                entity.getQuantity()
        );
    }

    public static OrderItemEntity toEntityItem(OrderItem item, Long orderId) {
        if (item == null) {
            return null;
        }
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrderId(orderId);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setQuantity(item.getQuantity());
        return entity;
    }

}

package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order createOrder(Long userId, OrderStatus status, List<OrderItem> items, BigDecimal total,
                      String shippingAddress, String shippingCity, String shippingZip);

    Optional<Order> findById(Long orderId);

}

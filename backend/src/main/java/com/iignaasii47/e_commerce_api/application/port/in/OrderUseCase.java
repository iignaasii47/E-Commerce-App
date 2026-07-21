package com.iignaasii47.e_commerce_api.application.port.in;

import com.iignaasii47.e_commerce_api.domain.model.Order;

import java.util.Optional;

public interface OrderUseCase {

    Order createOrder(String shippingAddress, String shippingCity, String shippingZip);

    Optional<Order> getOrderById(Long orderId);

}

package com.iignaasii47.e_commerce_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {

    private final Long id;
    private final Long userId;
    private final OrderStatus status;
    private final List<OrderItem> items;
    private final BigDecimal total;
    private final String shippingAddress;
    private final String shippingCity;
    private final String shippingZip;
    private final LocalDateTime createdAt;

    public Order(Long id, Long userId, OrderStatus status, List<OrderItem> items, BigDecimal total,
                 String shippingAddress, String shippingCity, String shippingZip, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.items = items;
        this.total = total;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingZip = shippingZip;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public String getShippingZip() {
        return shippingZip;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

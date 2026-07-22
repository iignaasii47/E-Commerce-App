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

    private Order(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.status = builder.status;
        this.items = builder.items;
        this.total = builder.total;
        this.shippingAddress = builder.shippingAddress;
        this.shippingCity = builder.shippingCity;
        this.shippingZip = builder.shippingZip;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Long id;
        private Long userId;
        private OrderStatus status;
        private List<OrderItem> items;
        private BigDecimal total;
        private String shippingAddress;
        private String shippingCity;
        private String shippingZip;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder total(BigDecimal total) {
            this.total = total;
            return this;
        }

        public Builder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder shippingCity(String shippingCity) {
            this.shippingCity = shippingCity;
            return this;
        }

        public Builder shippingZip(String shippingZip) {
            this.shippingZip = shippingZip;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

}

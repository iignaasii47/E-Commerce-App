package com.iignaasii47.e_commerce_api.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem {

    private final Long id;
    private final Long userId;
    private final Long productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    public CartItem(Long id, Long userId, Long productId, String productName,
                    BigDecimal unitPrice, int quantity) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartRepository {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findById(Long cartItemId);

    CartItem addItem(Long userId, Long productId, String productName, BigDecimal unitPrice, int quantity);

    void removeItem(Long userId, Long cartItemId);

    CartItem findByUserAndProduct(Long userId, Long productId);

    void updateQuantity(Long cartItemId, int quantity);

    void clearCart(Long userId);

}

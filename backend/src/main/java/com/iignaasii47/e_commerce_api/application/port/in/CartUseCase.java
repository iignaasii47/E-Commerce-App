package com.iignaasii47.e_commerce_api.application.port.in;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import java.util.List;

public interface CartUseCase {

    List<CartItem> getCart();

    CartItem addToCart(Long productId, int quantity);

    void removeFromCart(Long cartItemId);

}

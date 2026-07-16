package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.CartItemResponse;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartUseCase cartUseCase;

    public CartController(CartUseCase cartUseCase) {
        this.cartUseCase = cartUseCase;
    }

    @GetMapping
    public List<CartItemResponse> getCart() {
        return cartUseCase.getCart().stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @PostMapping
    public CartItemResponse addToCart(@RequestParam Long productId,
                                       @RequestParam(defaultValue = "1") int quantity) {
        CartItem item = cartUseCase.addToCart(productId, quantity);
        return CartItemResponse.from(item);
    }

    @DeleteMapping("/{cartItemId}")
    public Map<String, String> removeFromCart(@PathVariable Long cartItemId) {
        cartUseCase.removeFromCart(cartItemId);
        return Map.of("status", "removed");
    }

}

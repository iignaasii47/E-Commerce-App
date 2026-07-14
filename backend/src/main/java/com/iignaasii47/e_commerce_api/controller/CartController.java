package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.CartItemResponse;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartUseCase cartUseCase;
    private final UserIdExtractor userIdExtractor;

    public CartController(CartUseCase cartUseCase, UserIdExtractor userIdExtractor) {
        this.cartUseCase = cartUseCase;
        this.userIdExtractor = userIdExtractor;
    }

    @GetMapping
    public List<CartItemResponse> getCart(@RequestHeader("Authorization") String authHeader) {
        Long userId = userIdExtractor.extract(authHeader);
        return cartUseCase.getCart(userId).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @PostMapping
    public CartItemResponse addToCart(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam Long productId,
                                       @RequestParam(defaultValue = "1") int quantity) {
        Long userId = userIdExtractor.extract(authHeader);
        CartItem item = cartUseCase.addToCart(userId, productId, quantity);
        return CartItemResponse.from(item);
    }

    @DeleteMapping("/{cartItemId}")
    public Map<String, String> removeFromCart(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long cartItemId) {
        Long userId = userIdExtractor.extract(authHeader);
        cartUseCase.removeFromCart(userId, cartItemId);
        return Map.of("status", "removed");
    }

}

package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.CartItemResponse;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Cart", description = "Shopping cart management for authenticated users")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartUseCase cartUseCase;

    public CartController(CartUseCase cartUseCase) {
        this.cartUseCase = cartUseCase;
    }

    @GetMapping
    @Operation(summary = "View cart contents",
            description = "Returns all items currently in the authenticated user's shopping cart.")
    @ApiResponse(responseCode = "200", description = "Cart items returned successfully")
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content)
    public List<CartItemResponse> getCart() {
        return cartUseCase.getCart().stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Add product to cart",
            description = "Adds a product to the cart. If the product is already in the cart, increments the quantity.")
    @ApiResponse(responseCode = "200", description = "Item added to cart successfully")
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content)
    public CartItemResponse addToCart(
            @Parameter(description = "ID of the product to add", required = true, example = "5")
            @RequestParam Long productId,
            @Parameter(description = "Quantity to add (default: 1)", example = "2")
            @RequestParam(defaultValue = "1") int quantity) {
        CartItem item = cartUseCase.addToCart(productId, quantity);
        return CartItemResponse.from(item);
    }

    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "Remove item from cart",
            description = "Removes a single cart item by its ID.")
    @ApiResponse(responseCode = "200", description = "Item removed successfully",
            content = @Content(schema = @Schema(example = "{\"status\":\"removed\"}")))
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content)
    public Map<String, String> removeFromCart(
            @Parameter(description = "Cart item identifier to remove", example = "7")
            @PathVariable Long cartItemId) {
        cartUseCase.removeFromCart(cartItemId);
        return Map.of("status", "removed");
    }

}

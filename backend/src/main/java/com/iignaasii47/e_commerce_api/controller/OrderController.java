package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.OrderUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.OrderRequest;
import com.iignaasii47.e_commerce_api.controller.dto.OrderResponse;
import com.iignaasii47.e_commerce_api.domain.model.Order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order placement and retrieval for authenticated users")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order",
            description = "Creates an order from the current cart items. Validates stock, decrements inventory, and clears the cart.")
    @ApiResponse(responseCode = "201", description = "Order placed successfully")
    @ApiResponse(responseCode = "400", description = "Empty cart or insufficient stock",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content)
    public OrderResponse createOrder(
            @Parameter(description = "Order details including shipping address", required = true)
            @RequestBody @Valid OrderRequest request) {
        Order order = orderUseCase.createOrder(
                request.getShippingAddress(),
                request.getShippingCity(),
                request.getShippingZip());
        return OrderResponse.from(order);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID",
            description = "Returns the order details for a given order ID.")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content)
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content)
    public OrderResponse getOrder(
            @Parameter(description = "Order identifier", required = true, example = "1")
            @PathVariable Long orderId) {
        Order order = orderUseCase.getOrderById(orderId)
                .orElseThrow(() -> new com.iignaasii47.e_commerce_api.domain.exception
                        .OrderNotFoundException("Order not found with id: " + orderId));
        return OrderResponse.from(order);
    }

}

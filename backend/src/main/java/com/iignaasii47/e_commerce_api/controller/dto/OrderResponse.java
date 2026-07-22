package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Order confirmation details")
@Getter
@NoArgsConstructor
public class OrderResponse {

    @Schema(description = "Unique order identifier", example = "1")
    private Long id;

    @Schema(description = "Order status", example = "CONFIRMED")
    private String status;

    @Schema(description = "Order total", example = "299.98")
    private BigDecimal total;

    @Schema(description = "Items in the order")
    private List<OrderItemResponse> items;

    @Schema(description = "Shipping street address", example = "123 Terminal St")
    private String shippingAddress;

    @Schema(description = "Shipping city", example = "San Francisco")
    private String shippingCity;

    @Schema(description = "Shipping ZIP code", example = "94102")
    private String shippingZip;

    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.id = order.getId();
        response.status = order.getStatus().name();
        response.total = order.getTotal();
        response.items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        response.shippingAddress = order.getShippingAddress();
        response.shippingCity = order.getShippingCity();
        response.shippingZip = order.getShippingZip();
        response.createdAt = order.getCreatedAt();
        return response;
    }

}

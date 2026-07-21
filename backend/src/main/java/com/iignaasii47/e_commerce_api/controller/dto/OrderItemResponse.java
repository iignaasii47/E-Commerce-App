package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.OrderItem;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "A single item within an order")
@Getter
@NoArgsConstructor
public class OrderItemResponse {

    @Schema(description = "Unique order item identifier", example = "1")
    private Long id;

    @Schema(description = "ID of the ordered product", example = "5")
    private Long productId;

    @Schema(description = "Product name at time of order", example = "Mechanical Keyboard MK-750")
    private String productName;

    @Schema(description = "Price per unit at time of order", example = "149.99")
    private BigDecimal unitPrice;

    @Schema(description = "Number of units ordered", example = "2")
    private int quantity;

    @Schema(description = "Calculated subtotal (unitPrice * quantity)", example = "299.98")
    private BigDecimal subtotal;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.id = item.getId();
        response.productId = item.getProductId();
        response.productName = item.getProductName();
        response.unitPrice = item.getUnitPrice();
        response.quantity = item.getQuantity();
        response.subtotal = item.getSubtotal();
        return response;
    }

}

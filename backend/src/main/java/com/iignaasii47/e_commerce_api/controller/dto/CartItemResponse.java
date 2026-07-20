package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "A single item in the user's shopping cart")
@Getter
@NoArgsConstructor
public class CartItemResponse {

    @Schema(description = "Unique cart item identifier", example = "1")
    private Long id;
    @Schema(description = "ID of the product added to cart", example = "5")
    private Long productId;
    @Schema(description = "Name of the product at time of addition", example = "Mechanical Keyboard MK-750")
    private String productName;
    @Schema(description = "Price per unit at time of addition", example = "149.99")
    private BigDecimal unitPrice;
    @Schema(description = "Number of units", example = "2")
    private int quantity;
    @Schema(description = "Calculated subtotal (unitPrice * quantity)", example = "299.98")
    private BigDecimal subtotal;

    public static CartItemResponse from(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.id = item.getId();
        response.productId = item.getProductId();
        response.productName = item.getProductName();
        response.unitPrice = item.getUnitPrice();
        response.quantity = item.getQuantity();
        response.subtotal = item.getSubtotal();
        return response;
    }

}

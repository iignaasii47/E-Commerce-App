package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Request to create a new order from the current cart")
@Getter
@NoArgsConstructor
public class OrderRequest {

    @NotBlank(message = "Shipping address is required")
    @Schema(description = "Street address for delivery", example = "123 Terminal St")
    private String shippingAddress;

    @NotBlank(message = "Shipping city is required")
    @Schema(description = "City for delivery", example = "San Francisco")
    private String shippingCity;

    @NotBlank(message = "Shipping ZIP code is required")
    @Schema(description = "ZIP code for delivery", example = "94102")
    private String shippingZip;

}

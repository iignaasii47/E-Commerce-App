package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for authenticating an existing user")
public record LoginRequest(
        @NotBlank @Email
        @Schema(description = "Registered email address", example = "john@example.com")
        String email,
        @NotBlank
        @Schema(description = "User password", example = "s3cur3P@ss!")
        String password
) {
}

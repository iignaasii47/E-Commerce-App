package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for registering a new user account")
public record CreateUserRequest(
        @NotBlank
        @Schema(description = "Unique username for login", example = "john_doe")
        String username,
        @NotBlank @Email
        @Schema(description = "Valid email address", example = "john@example.com")
        String email,
        @NotBlank @Size(min = 8)
        @Schema(description = "Password with at least 8 characters", example = "s3cur3P@ss!")
        String password
) {
}

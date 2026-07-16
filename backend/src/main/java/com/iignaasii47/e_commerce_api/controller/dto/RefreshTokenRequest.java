package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for refreshing an access token or logging out")
public record RefreshTokenRequest(
        @Schema(description = "The refresh token obtained during login",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTc2ODUwMDAwMCwiZXhwIjoxNzY5MTA0ODAwfQ.xyz789")
        @NotBlank String refreshToken
) {
}

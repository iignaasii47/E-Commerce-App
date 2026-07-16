package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Authentication response containing user data and tokens")
public class LoginResponse extends UserResponse {

    @Schema(description = "JWT access token to use in Authorization header for authenticated endpoints (expires in 15 minutes)",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNzY4NTAwMDAwLCJleHAiOjE3Njg1MDA5MDB9.abc123")
    private String accessToken;

    @Schema(description = "Refresh token used to obtain a new access token (expires in 7 days)",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTc2ODUwMDAwMCwiZXhwIjoxNzY5MTA0ODAwfQ.xyz789")
    private String refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String username, String email, LocalDateTime createdAt,
                          String accessToken, String refreshToken) {
        super(id, username, email, createdAt);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse from(Authentication authentication) {
        var user = authentication.getUser();
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getCreatedAt(), authentication.getAccessToken(), authentication.getRefreshToken());
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}

package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Authentication response containing user data and access token")
public class LoginResponse extends UserResponse {

    @Schema(description = "JWT access token to use in Authorization header for authenticated endpoints",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJpYXQiOjE3Njg1MDAwMDAsImV4cCI6MTc2ODU4NjQwMH0.abc123")
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String username, String email, LocalDateTime createdAt, String token) {
        super(id, username, email, createdAt);
        this.token = token;
    }

    public static LoginResponse from(Authentication authentication) {
        var user = authentication.getUser();
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getCreatedAt(), authentication.getToken());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.User;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Public user profile information")
public class UserResponse {

    @Schema(description = "Unique user identifier", example = "1")
    private Long id;
    @Schema(description = "Unique username", example = "john_doe")
    private String username;
    @Schema(description = "User email address", example = "john@example.com")
    private String email;
    @Schema(description = "Account creation timestamp (UTC)", example = "2026-01-01T12:00:00")
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}

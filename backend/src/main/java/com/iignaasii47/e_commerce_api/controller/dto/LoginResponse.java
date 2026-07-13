package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;

import java.time.LocalDateTime;

public class LoginResponse {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String username, String email, LocalDateTime createdAt, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.token = token;
    }

    public static LoginResponse from(Authentication authentication) {
        var user = authentication.getUser();
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(), authentication.getToken());
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}

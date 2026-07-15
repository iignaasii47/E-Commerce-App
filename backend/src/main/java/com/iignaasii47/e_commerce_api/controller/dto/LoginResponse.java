package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;

import java.time.LocalDateTime;

public class LoginResponse extends UserResponse {

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

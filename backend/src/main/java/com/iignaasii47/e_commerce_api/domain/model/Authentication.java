package com.iignaasii47.e_commerce_api.domain.model;

public class Authentication {

    private final User user;
    private final String accessToken;
    private final String refreshToken;

    public Authentication(User user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}

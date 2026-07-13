package com.iignaasii47.e_commerce_api.domain.model;

public class Authentication {

    private final User user;
    private final String token;

    public Authentication(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

}

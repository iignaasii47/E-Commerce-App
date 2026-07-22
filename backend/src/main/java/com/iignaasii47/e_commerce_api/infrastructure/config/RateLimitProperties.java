package com.iignaasii47.e_commerce_api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private int login = 5;
    private int registration = 3;
    private int tokenRefresh = 10;
    private int chat = 30;
    private int windowSeconds = 60;

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRegistration() {
        return registration;
    }

    public void setRegistration(int registration) {
        this.registration = registration;
    }

    public int getTokenRefresh() {
        return tokenRefresh;
    }

    public void setTokenRefresh(int tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    public int getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(int windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

}

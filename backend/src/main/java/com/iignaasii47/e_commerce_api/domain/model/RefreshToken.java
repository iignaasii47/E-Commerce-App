package com.iignaasii47.e_commerce_api.domain.model;

import java.time.LocalDateTime;

public class RefreshToken {

    private final Long id;
    private final String token;
    private final Long userId;
    private final LocalDateTime expiryDate;
    private final boolean revoked;
    private final LocalDateTime createdAt;

    public RefreshToken(Long id, String token, Long userId, LocalDateTime expiryDate,
                         boolean revoked, LocalDateTime createdAt) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now(java.time.ZoneOffset.UTC));
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isValid() {
        return !isExpired() && !isRevoked();
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}

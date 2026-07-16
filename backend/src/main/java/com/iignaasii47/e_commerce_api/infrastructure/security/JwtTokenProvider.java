package com.iignaasii47.e_commerce_api.infrastructure.security;

import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenService {

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.access-token-expiration-ms}") long accessExpirationMs,
                             @Value("${jwt.refresh-token-expiration-ms}") long refreshExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    public String generateAccessToken(Long userId, String username) {
        return generateToken(userId, username, TOKEN_TYPE_ACCESS, accessExpirationMs);
    }

    @Override
    public String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, TOKEN_TYPE_REFRESH, refreshExpirationMs);
    }

    @Override
    public Long validateAccessTokenAndGetUserId(String token) {
        Claims claims = parseClaims(token);
        String type = claims.get("type", String.class);
        if (!TOKEN_TYPE_ACCESS.equals(type)) {
            throw new IllegalArgumentException("Token is not an access token");
        }
        return claims.get("userId", Long.class);
    }

    @Override
    public Long validateRefreshTokenAndGetUserId(String token) {
        Claims claims = parseClaims(token);
        String type = claims.get("type", String.class);
        if (!TOKEN_TYPE_REFRESH.equals(type)) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }
        return claims.get("userId", Long.class);
    }

    private String generateToken(Long userId, String username, String type, long expirationMs) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}

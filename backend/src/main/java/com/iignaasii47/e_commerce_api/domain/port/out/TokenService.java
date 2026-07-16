package com.iignaasii47.e_commerce_api.domain.port.out;

public interface TokenService {

    String generateAccessToken(Long userId, String username);

    String generateRefreshToken(Long userId, String username);

    Long validateAccessTokenAndGetUserId(String token);

    Long validateRefreshTokenAndGetUserId(String token);

}

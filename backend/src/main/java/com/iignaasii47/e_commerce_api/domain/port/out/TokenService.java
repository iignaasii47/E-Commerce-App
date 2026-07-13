package com.iignaasii47.e_commerce_api.domain.port.out;

public interface TokenService {

    String generateToken(Long userId, String username);

}

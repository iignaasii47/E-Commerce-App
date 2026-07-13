package com.iignaasii47.e_commerce_api.domain.port.out;

public interface PasswordEncryption {

    String encrypt(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

}

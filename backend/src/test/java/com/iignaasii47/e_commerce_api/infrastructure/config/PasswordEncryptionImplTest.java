package com.iignaasii47.e_commerce_api.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncryptionImplTest {

    private final PasswordEncryptionImpl passwordEncryption = new PasswordEncryptionImpl();

    @Test
    void shouldEncryptAndNotReturnRawPassword() {
        String raw = "mySecret123";

        String encrypted = passwordEncryption.encrypt(raw);

        assertThat(encrypted).isNotEqualTo(raw).startsWith("$2a$");
    }

    @Test
    void shouldMatchCorrectPassword() {
        String raw = "mySecret123";
        String encrypted = passwordEncryption.encrypt(raw);

        assertThat(passwordEncryption.matches(raw, encrypted)).isTrue();
    }

    @Test
    void shouldNotMatchWrongPassword() {
        String raw = "mySecret123";
        String encrypted = passwordEncryption.encrypt(raw);

        assertThat(passwordEncryption.matches("wrongPassword", encrypted)).isFalse();
    }

}

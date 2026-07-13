package com.iignaasii47.e_commerce_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidCredentialsExceptionTest {

    @Test
    void shouldStoreMessage() {
        InvalidCredentialsException exception = new InvalidCredentialsException("bad credentials");

        assertThat(exception.getMessage()).isEqualTo("bad credentials");
    }

    @Test
    void shouldBeRuntimeException() {
        InvalidCredentialsException exception = new InvalidCredentialsException("error");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

}

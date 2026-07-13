package com.iignaasii47.e_commerce_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateUserExceptionTest {

    @Test
    void shouldStoreMessage() {
        DuplicateUserException exception = new DuplicateUserException("user exists");

        assertThat(exception.getMessage()).isEqualTo("user exists");
    }

    @Test
    void shouldBeRuntimeException() {
        DuplicateUserException exception = new DuplicateUserException("error");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

}

package com.iignaasii47.e_commerce_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiServiceExceptionTest {

    @Test
    void shouldStoreMessage() {
        AiServiceException ex = new AiServiceException("service error");

        assertThat(ex.getMessage()).isEqualTo("service error");
    }

    @Test
    void shouldBeRuntimeException() {
        AiServiceException ex = new AiServiceException("error");

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

}

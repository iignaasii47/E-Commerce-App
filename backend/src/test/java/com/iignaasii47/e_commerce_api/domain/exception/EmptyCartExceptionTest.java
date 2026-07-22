package com.iignaasii47.e_commerce_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmptyCartExceptionTest {

    @Test
    void shouldStoreMessage() {
        EmptyCartException ex = new EmptyCartException("cart is empty");

        assertThat(ex.getMessage()).isEqualTo("cart is empty");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

}

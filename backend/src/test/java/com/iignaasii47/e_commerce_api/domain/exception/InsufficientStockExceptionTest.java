package com.iignaasii47.e_commerce_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsufficientStockExceptionTest {

    @Test
    void shouldStoreMessage() {
        InsufficientStockException ex = new InsufficientStockException("not enough stock");

        assertThat(ex.getMessage()).isEqualTo("not enough stock");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

}

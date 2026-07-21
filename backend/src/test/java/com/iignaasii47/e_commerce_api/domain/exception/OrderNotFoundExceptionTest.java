package com.iignaasii47.e_commerce_api.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotFoundExceptionTest {

    @Test
    void shouldStoreMessage() {
        OrderNotFoundException ex = new OrderNotFoundException("order not found with id: 1");

        assertThat(ex.getMessage()).isEqualTo("order not found with id: 1");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

}

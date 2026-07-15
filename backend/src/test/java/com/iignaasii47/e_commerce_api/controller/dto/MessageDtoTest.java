package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDtoTest {

    @Test
    void shouldStoreRoleAndContent() {
        MessageDto dto = new MessageDto("user", "hello");

        assertThat(dto.role()).isEqualTo("user");
        assertThat(dto.content()).isEqualTo("hello");
    }

}

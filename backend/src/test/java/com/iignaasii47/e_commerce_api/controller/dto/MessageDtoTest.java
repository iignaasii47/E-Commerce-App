package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDtoTest {

    @Test
    void shouldStoreRoleAndContent() {
        MessageDto dto = new MessageDto("user", "hello");

        assertThat(dto.getRole()).isEqualTo("user");
        assertThat(dto.getContent()).isEqualTo("hello");
    }

    @Test
    void shouldCreateEmpty() {
        MessageDto dto = new MessageDto();

        assertThat(dto.getRole()).isNull();
        assertThat(dto.getContent()).isNull();
    }

    @Test
    void shouldSetRole() {
        MessageDto dto = new MessageDto();
        dto.setRole("bot");

        assertThat(dto.getRole()).isEqualTo("bot");
    }

    @Test
    void shouldSetContent() {
        MessageDto dto = new MessageDto();
        dto.setContent("response");

        assertThat(dto.getContent()).isEqualTo("response");
    }

}

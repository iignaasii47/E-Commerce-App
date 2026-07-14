package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatResponseTest {

    @Test
    void shouldCreateFromReply() {
        ChatResponse response = ChatResponse.of("AI reply text");

        assertThat(response.getReply()).isEqualTo("AI reply text");
    }

    @Test
    void shouldCreateEmpty() {
        ChatResponse response = new ChatResponse();

        assertThat(response.getReply()).isNull();
    }

    @Test
    void shouldSetReply() {
        ChatResponse response = new ChatResponse();
        response.setReply("hello");

        assertThat(response.getReply()).isEqualTo("hello");
    }

}

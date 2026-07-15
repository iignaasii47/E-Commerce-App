package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRequestTest {

    @Test
    void shouldStoreMessageAndHistory() {
        List<MessageDto> history = List.of(new MessageDto("user", "previous message"));
        ChatRequest request = new ChatRequest("current message", history);

        assertThat(request.getMessage()).isEqualTo("current message");
        assertThat(request.getHistory()).hasSize(1);
        assertThat(request.getHistory().get(0).role()).isEqualTo("user");
        assertThat(request.getHistory().get(0).content()).isEqualTo("previous message");
    }

    @Test
    void shouldDefaultHistoryToEmpty() {
        ChatRequest request = new ChatRequest();

        assertThat(request.getHistory()).isNotNull().isEmpty();
    }

    @Test
    void shouldSetMessage() {
        ChatRequest request = new ChatRequest();
        request.setMessage("hello");

        assertThat(request.getMessage()).isEqualTo("hello");
    }

    @Test
    void shouldSetHistory() {
        ChatRequest request = new ChatRequest();
        List<MessageDto> history = List.of(new MessageDto("user", "hi"));
        request.setHistory(history);

        assertThat(request.getHistory()).isEqualTo(history);
    }

}

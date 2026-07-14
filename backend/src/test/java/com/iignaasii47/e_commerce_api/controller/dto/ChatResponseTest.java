package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.ChatResult;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatResponseTest {

    @Test
    void shouldCreateFromChatResult() {
        ChatResult result = new ChatResult("AI reply text", List.of("search_products", "add_to_cart"));

        ChatResponse response = ChatResponse.from(result);

        assertThat(response.getReply()).isEqualTo("AI reply text");
        assertThat(response.getToolsUsed()).containsExactly("search_products", "add_to_cart");
    }

    @Test
    void shouldCreateFromChatResultWithNoTools() {
        ChatResult result = ChatResult.of("AI reply text");

        ChatResponse response = ChatResponse.from(result);

        assertThat(response.getReply()).isEqualTo("AI reply text");
        assertThat(response.getToolsUsed()).isEmpty();
    }

    @Test
    void shouldCreateEmpty() {
        ChatResponse response = new ChatResponse();

        assertThat(response.getReply()).isNull();
        assertThat(response.getToolsUsed()).isEmpty();
    }

    @Test
    void shouldSetReply() {
        ChatResponse response = new ChatResponse();
        response.setReply("hello");

        assertThat(response.getReply()).isEqualTo("hello");
    }

    @Test
    void shouldSetToolsUsed() {
        ChatResponse response = new ChatResponse();
        response.setToolsUsed(List.of("view_cart"));

        assertThat(response.getToolsUsed()).containsExactly("view_cart");
    }

}

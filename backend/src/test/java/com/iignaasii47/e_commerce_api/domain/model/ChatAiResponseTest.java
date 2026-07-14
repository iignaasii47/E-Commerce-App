package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatAiResponseTest {

    @Test
    void shouldCreateTextResponse() {
        ChatAiResponse response = ChatAiResponse.text("Hello");

        assertThat(response.getContent()).isEqualTo("Hello");
        assertThat(response.getToolCalls()).isNull();
        assertThat(response.hasToolCalls()).isFalse();
    }

    @Test
    void shouldCreateToolCallsResponse() {
        ChatToolCall call = new ChatToolCall("id1", "search", java.util.Map.of());
        ChatAiResponse response = ChatAiResponse.toolCalls(java.util.List.of(call));

        assertThat(response.getContent()).isNull();
        assertThat(response.getToolCalls()).hasSize(1);
        assertThat(response.hasToolCalls()).isTrue();
    }

    @Test
    void shouldReturnFalseForEmptyToolCalls() {
        ChatAiResponse response = ChatAiResponse.toolCalls(java.util.List.of());

        assertThat(response.hasToolCalls()).isFalse();
    }

}

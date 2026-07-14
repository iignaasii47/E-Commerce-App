package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    void shouldStoreRoleAndContent() {
        ChatMessage msg = new ChatMessage("user", "hello");

        assertThat(msg.getRole()).isEqualTo("user");
        assertThat(msg.getContent()).isEqualTo("hello");
    }

    @Test
    void shouldCreateUserMessage() {
        ChatMessage msg = ChatMessage.user("hello");

        assertThat(msg.getRole()).isEqualTo("user");
        assertThat(msg.getContent()).isEqualTo("hello");
    }

    @Test
    void shouldCreateAssistantMessage() {
        ChatMessage msg = ChatMessage.assistant("response");

        assertThat(msg.getRole()).isEqualTo("assistant");
        assertThat(msg.getContent()).isEqualTo("response");
    }

    @Test
    void shouldCreateSystemMessage() {
        ChatMessage msg = ChatMessage.system("instructions");

        assertThat(msg.getRole()).isEqualTo("system");
        assertThat(msg.getContent()).isEqualTo("instructions");
    }

    @Test
    void shouldEqualWhenSameRoleAndContent() {
        ChatMessage msg1 = new ChatMessage("user", "hello");
        ChatMessage msg2 = new ChatMessage("user", "hello");

        assertThat(msg1).isEqualTo(msg2).hasSameHashCodeAs(msg2);
    }

    @Test
    void shouldNotEqualWhenDifferentRole() {
        ChatMessage msg1 = new ChatMessage("user", "hello");
        ChatMessage msg2 = new ChatMessage("assistant", "hello");

        assertThat(msg1).isNotEqualTo(msg2);
    }

    @Test
    void shouldNotEqualWhenDifferentContent() {
        ChatMessage msg1 = new ChatMessage("user", "hello");
        ChatMessage msg2 = new ChatMessage("user", "world");

        assertThat(msg1).isNotEqualTo(msg2);
    }

    @Test
    void shouldNotEqualNull() {
        ChatMessage msg = new ChatMessage("user", "hello");

        assertThat(msg).isNotEqualTo(null);
    }

    @Test
    void shouldNotEqualDifferentType() {
        ChatMessage msg = new ChatMessage("user", "hello");

        assertThat(msg).isNotEqualTo("not a message");
    }

    @Test
    void shouldHaveToString() {
        ChatMessage msg = new ChatMessage("user", "hello");

        assertThat(msg.toString()).contains("user", "hello");
    }

}

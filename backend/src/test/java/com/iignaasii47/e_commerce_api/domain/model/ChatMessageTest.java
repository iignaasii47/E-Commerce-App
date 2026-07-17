package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    void shouldCreateUserMessage() {
        ChatMessage message = ChatMessage.user("hello");

        assertThat(message.getRole()).isEqualTo("user");
        assertThat(message.getContent()).isEqualTo("hello");
        assertThat(message.getToolCallId()).isNull();
    }

    @Test
    void shouldCreateAssistantMessage() {
        ChatMessage message = ChatMessage.assistant("response");

        assertThat(message.getRole()).isEqualTo("assistant");
        assertThat(message.getContent()).isEqualTo("response");
        assertThat(message.getToolCallId()).isNull();
    }

    @Test
    void shouldCreateSystemMessage() {
        ChatMessage message = ChatMessage.system("instructions");

        assertThat(message.getRole()).isEqualTo("system");
        assertThat(message.getContent()).isEqualTo("instructions");
        assertThat(message.getToolCallId()).isNull();
    }

    @Test
    void shouldCreateToolMessage() {
        ChatMessage message = ChatMessage.tool("call-1", "result");

        assertThat(message.getRole()).isEqualTo("tool");
        assertThat(message.getContent()).isEqualTo("result");
        assertThat(message.getToolCallId()).isEqualTo("call-1");
    }

    @Test
    void shouldCreateWithTwoArgConstructor() {
        ChatMessage message = new ChatMessage("assistant", "Hello world");

        assertThat(message.getRole()).isEqualTo("assistant");
        assertThat(message.getContent()).isEqualTo("Hello world");
        assertThat(message.getToolCallId()).isNull();
    }

    @Test
    void shouldNotBeEqualToNull() {
        ChatMessage message = ChatMessage.user("hi");

        assertThat(message).isNotNull();
    }

    @Test
    void shouldBeEqualToIdenticalMessage() {
        ChatMessage a = new ChatMessage("user", "hello", null);
        ChatMessage b = new ChatMessage("user", "hello", null);

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }

    @Test
    void shouldNotBeEqualToDifferentRole() {
        ChatMessage a = new ChatMessage("user", "hello", null);
        ChatMessage b = new ChatMessage("assistant", "hello", null);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void shouldNotBeEqualToDifferentContent() {
        ChatMessage a = new ChatMessage("user", "hello", null);
        ChatMessage b = new ChatMessage("user", "world", null);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void shouldNotBeEqualToDifferentToolCallId() {
        ChatMessage a = new ChatMessage("tool", "result", "call-1");
        ChatMessage b = new ChatMessage("tool", "result", "call-2");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void shouldBeEqualToItself() {
        ChatMessage message = ChatMessage.user("hello");

        assertThat(message).isEqualTo(message);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        ChatMessage message = ChatMessage.user("hello");

        assertThat(message).isNotEqualTo("hello");
    }

    @Test
    void shouldProduceToString() {
        ChatMessage message = new ChatMessage("assistant", "Hello!");

        assertThat(message.toString()).isEqualTo("assistant: Hello!");
    }
}

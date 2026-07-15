package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatResultTest {

    @Test
    void shouldStoreReplyAndToolsUsed() {
        ChatResult result = new ChatResult("Hello", List.of("search_products", "add_to_cart"));

        assertThat(result.getReply()).isEqualTo("Hello");
        assertThat(result.getToolsUsed()).containsExactly("search_products", "add_to_cart");
    }

    @Test
    void shouldCopyToolListImmutably() {
        List<String> mutable = new java.util.ArrayList<>(List.of("tool1"));
        ChatResult result = new ChatResult("reply", mutable);

        mutable.add("tool2");

        assertThat(result.getToolsUsed()).hasSize(1).containsExactly("tool1");
    }

    @Test
    void shouldCreateWithFactoryMethod() {
        ChatResult result = ChatResult.of("Simple reply");

        assertThat(result.getReply()).isEqualTo("Simple reply");
        assertThat(result.getToolsUsed()).isEmpty();
    }

    @Test
    void shouldHandleEmptyTools() {
        ChatResult result = new ChatResult("Hi", List.of());

        assertThat(result.getToolsUsed()).isEmpty();
    }

    @Test
    void shouldHandleNullReply() {
        ChatResult result = new ChatResult(null, List.of("tool"));

        assertThat(result.getReply()).isNull();
        assertThat(result.getToolsUsed()).containsExactly("tool");
    }

}

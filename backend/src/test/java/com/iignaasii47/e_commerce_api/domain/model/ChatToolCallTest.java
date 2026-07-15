package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChatToolCallTest {

    @Test
    void shouldStoreFields() {
        Map<String, Object> args = Map.of("product_id", 1, "quantity", 2);
        ChatToolCall call = new ChatToolCall("call_123", "add_to_cart", args);

        assertThat(call.getId()).isEqualTo("call_123");
        assertThat(call.getFunctionName()).isEqualTo("add_to_cart");
        assertThat(call.getArguments()).containsEntry("product_id", 1).containsEntry("quantity", 2);
    }

    @Test
    void shouldEqualWhenSameId() {
        ChatToolCall call1 = new ChatToolCall("call_1", "f1", Map.of());
        ChatToolCall call2 = new ChatToolCall("call_1", "f2", Map.of("a", 1));

        assertThat(call1).isEqualTo(call2).hasSameHashCodeAs(call2);
    }

    @Test
    void shouldNotEqualWhenDifferentId() {
        ChatToolCall call1 = new ChatToolCall("call_1", "f", Map.of());
        ChatToolCall call2 = new ChatToolCall("call_2", "f", Map.of());

        assertThat(call1).isNotEqualTo(call2);
    }

}

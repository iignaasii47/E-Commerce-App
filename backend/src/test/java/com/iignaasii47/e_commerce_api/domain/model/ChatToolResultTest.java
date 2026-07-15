package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatToolResultTest {

    @Test
    void shouldStoreAllFields() {
        ChatToolResult result = new ChatToolResult("call_123", "search_products", "Found 3 products");

        assertThat(result.getToolCallId()).isEqualTo("call_123");
        assertThat(result.getToolName()).isEqualTo("search_products");
        assertThat(result.getResult()).isEqualTo("Found 3 products");
    }

    @Test
    void shouldHandleEmptyResult() {
        ChatToolResult result = new ChatToolResult("call_1", "view_cart", "");

        assertThat(result.getToolCallId()).isEqualTo("call_1");
        assertThat(result.getResult()).isEmpty();
    }

    @Test
    void shouldHandleNullValues() {
        ChatToolResult result = new ChatToolResult(null, null, null);

        assertThat(result.getToolCallId()).isNull();
        assertThat(result.getToolName()).isNull();
        assertThat(result.getResult()).isNull();
    }

    @Test
    void shouldPreserveErrorMessages() {
        ChatToolResult result = new ChatToolResult("call_1", "add_to_cart", "Error: Product not found");

        assertThat(result.getToolName()).isEqualTo("add_to_cart");
        assertThat(result.getResult()).startsWith("Error:");
    }

}

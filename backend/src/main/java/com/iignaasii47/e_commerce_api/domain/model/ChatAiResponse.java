package com.iignaasii47.e_commerce_api.domain.model;

import java.util.List;

public class ChatAiResponse {

    private final String content;
    private final List<ChatToolCall> toolCalls;

    public ChatAiResponse(String content, List<ChatToolCall> toolCalls) {
        this.content = content;
        this.toolCalls = toolCalls;
    }

    public static ChatAiResponse text(String content) {
        return new ChatAiResponse(content, null);
    }

    public static ChatAiResponse toolCalls(List<ChatToolCall> toolCalls) {
        return new ChatAiResponse(null, toolCalls);
    }

    public String getContent() {
        return content;
    }

    public List<ChatToolCall> getToolCalls() {
        return toolCalls;
    }

    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }

}

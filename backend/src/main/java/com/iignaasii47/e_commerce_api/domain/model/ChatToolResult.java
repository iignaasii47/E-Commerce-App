package com.iignaasii47.e_commerce_api.domain.model;

public class ChatToolResult {

    private final String toolCallId;
    private final String toolName;
    private final String result;

    public ChatToolResult(String toolCallId, String toolName, String result) {
        this.toolCallId = toolCallId;
        this.toolName = toolName;
        this.result = result;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public String getToolName() {
        return toolName;
    }

    public String getResult() {
        return result;
    }

}

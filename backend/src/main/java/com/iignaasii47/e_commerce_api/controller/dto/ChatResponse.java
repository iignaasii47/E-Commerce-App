package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.ChatResult;

import java.util.ArrayList;
import java.util.List;

public class ChatResponse {

    private String reply;
    private List<String> toolsUsed = new ArrayList<>();

    public ChatResponse() {
    }

    public ChatResponse(String reply, List<String> toolsUsed) {
        this.reply = reply;
        this.toolsUsed = toolsUsed;
    }

    public static ChatResponse from(ChatResult result) {
        return new ChatResponse(result.getReply(), result.getToolsUsed());
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<String> getToolsUsed() {
        return toolsUsed;
    }

    public void setToolsUsed(List<String> toolsUsed) {
        this.toolsUsed = toolsUsed;
    }

}

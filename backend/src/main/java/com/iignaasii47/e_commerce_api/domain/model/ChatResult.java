package com.iignaasii47.e_commerce_api.domain.model;

import java.util.ArrayList;
import java.util.List;

public class ChatResult {

    private final String reply;
    private final List<String> toolsUsed;

    public ChatResult(String reply, List<String> toolsUsed) {
        this.reply = reply;
        this.toolsUsed = List.copyOf(toolsUsed);
    }

    public static ChatResult of(String reply) {
        return new ChatResult(reply, List.of());
    }

    public String getReply() {
        return reply;
    }

    public List<String> getToolsUsed() {
        return toolsUsed;
    }

}

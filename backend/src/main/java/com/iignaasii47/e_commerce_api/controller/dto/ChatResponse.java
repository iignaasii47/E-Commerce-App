package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.ChatResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "AI assistant response with tool usage metadata")
public class ChatResponse {

    @Schema(description = "The assistant's textual response", example = "I found 3 wireless mice under $100. Here they are: ...")
    private String reply;
    @Schema(description = "Names of tool functions invoked during processing",
            example = "[\"search_products\", \"get_product\"]")
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

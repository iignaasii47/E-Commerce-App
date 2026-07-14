package com.iignaasii47.e_commerce_api.controller.dto;

public class ChatResponse {

    private String reply;

    public ChatResponse() {
    }

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public static ChatResponse of(String reply) {
        return new ChatResponse(reply);
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

}

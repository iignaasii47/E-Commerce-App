package com.iignaasii47.e_commerce_api.controller.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {

    @NotBlank
    private String message;

    private List<MessageDto> history = new ArrayList<>();

    public ChatRequest() {
    }

    public ChatRequest(String message, List<MessageDto> history) {
        this.message = message;
        this.history = history;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MessageDto> getHistory() {
        return history;
    }

    public void setHistory(List<MessageDto> history) {
        this.history = history;
    }

}

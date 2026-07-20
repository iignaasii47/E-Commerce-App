package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Chat message request with optional conversation history")
@Getter
@Setter
@NoArgsConstructor
public class ChatRequest {

    @NotBlank
    @Schema(description = "User's chat message", example = "Find me a wireless mouse")
    private String message;

    @Schema(description = "Previous conversation messages for context")
    private List<MessageDto> history = new ArrayList<>();

    public ChatRequest(String message, List<MessageDto> history) {
        this.message = message;
        this.history = history;
    }

    public List<MessageDto> getHistory() {
        if (history == null) {
            return new ArrayList<>();
        }
        return history;
    }

}

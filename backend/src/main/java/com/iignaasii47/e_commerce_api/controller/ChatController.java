package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.ChatRequest;
import com.iignaasii47.e_commerce_api.controller.dto.ChatResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.model.ChatResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "AI-powered shopping assistant")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatUseCase chatUseCase;

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping
    @Operation(summary = "Send a message to the AI assistant",
            description = "The assistant can search products, add items to your cart, remove items, "
                    + "view your cart, and answer questions about the developer. "
                    + "Include previous messages in the history for conversational context.")
    @ApiResponse(responseCode = "200", description = "Assistant replied successfully")
    @ApiResponse(responseCode = "400", description = "Validation error (blank message)",
            content = @Content(schema = @Schema(example = """
                    {"timestamp":"2026-07-16T10:30:00Z","status":400,"error":"Bad Request","message":"message: must not be blank"}""")))
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content)
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        List<ChatMessage> history = request.getHistory().stream()
                .map(dto -> new ChatMessage(mapRole(dto.role()), dto.content()))
                .toList();

        ChatResult result = chatUseCase.chat(request.getMessage(), history);
        return ChatResponse.from(result);
    }

    private String mapRole(String role) {
        if ("bot".equals(role)) {
            return "assistant";
        }
        return role;
    }

}

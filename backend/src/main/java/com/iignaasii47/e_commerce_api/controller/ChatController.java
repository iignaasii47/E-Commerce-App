package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.ChatRequest;
import com.iignaasii47.e_commerce_api.controller.dto.ChatResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.model.ChatResult;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatUseCase chatUseCase;

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping
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

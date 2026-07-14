package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.ChatRequest;
import com.iignaasii47.e_commerce_api.controller.dto.ChatResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.model.ChatResult;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatUseCase chatUseCase;
    private final UserIdExtractor userIdExtractor;

    public ChatController(ChatUseCase chatUseCase, UserIdExtractor userIdExtractor) {
        this.chatUseCase = chatUseCase;
        this.userIdExtractor = userIdExtractor;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request,
                              @RequestHeader("Authorization") String authHeader) {
        Long userId = userIdExtractor.extract(authHeader);
        List<ChatMessage> history = request.getHistory().stream()
                .map(dto -> new ChatMessage(mapRole(dto.getRole()), dto.getContent()))
                .toList();

        ChatResult result = chatUseCase.chat(request.getMessage(), history, userId);
        return ChatResponse.from(result);
    }

    private String mapRole(String role) {
        if ("bot".equals(role)) {
            return "assistant";
        }
        return role;
    }

}

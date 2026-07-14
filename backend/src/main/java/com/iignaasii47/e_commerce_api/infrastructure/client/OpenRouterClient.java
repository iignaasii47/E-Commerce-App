package com.iignaasii47.e_commerce_api.infrastructure.client;

import com.iignaasii47.e_commerce_api.domain.exception.AiServiceException;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenRouterClient implements AiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);
    private static final String ROLE_KEY = "role";
    private static final String CONTENT_KEY = "content";

    private final RestClient restClient;
    private final String model;

    public OpenRouterClient(OpenRouterProperties properties) {
        this.model = properties.getModel();
        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public String sendMessage(List<ChatMessage> history, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(ROLE_KEY, "system", CONTENT_KEY, systemPrompt));

        for (ChatMessage msg : history) {
            messages.add(Map.of(ROLE_KEY, msg.getRole(), CONTENT_KEY, msg.getContent()));
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", messages
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new AiServiceException("Empty response from AI service");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new AiServiceException("No choices in AI response");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null) {
            throw new AiServiceException("No message in AI response");
        }

        String content = (String) message.get(CONTENT_KEY);
        if (content == null) {
            throw new AiServiceException("No content in AI message");
        }

        log.info("OpenRouter response received, {} chars", content.length());
        return content;
    }

}

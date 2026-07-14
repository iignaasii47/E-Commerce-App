package com.iignaasii47.e_commerce_api.infrastructure.client;

import com.iignaasii47.e_commerce_api.domain.exception.AiServiceException;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
        String apiKey = properties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiServiceException(
                    "OpenRouter API key is not configured. "
                    + "Add OPENROUTER_API_KEY to the .env file in the backend directory.");
        }
        this.model = properties.getModel();
        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader("Authorization", "Bearer " + apiKey)
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
        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException e) {
            throw new AiServiceException(mapClientError(e));
        } catch (HttpServerErrorException e) {
            log.error("OpenRouter server error: {}", e.getMessage());
            throw new AiServiceException("OpenRouter service is temporarily unavailable. Please try again later.");
        }

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

    @SuppressWarnings("unchecked")
    private String mapClientError(HttpClientErrorException e) {
        HttpStatusCode status = e.getStatusCode();
        String openRouterMessage = extractOpenRouterErrorMessage(e);

        return switch (status.value()) {
            case 401 -> "Authentication failed. Please verify your OpenRouter API key.";
            case 402 -> openRouterMessage != null
                    ? openRouterMessage
                    : "Insufficient credits on your OpenRouter account.";
            case 403 -> "Access denied. Your API key may not have access to this model.";
            case 429 -> "Rate limit exceeded. Please wait and try again.";
            default -> openRouterMessage != null
                    ? openRouterMessage
                    : "OpenRouter API error: " + status.value();
        };
    }

    private String extractOpenRouterErrorMessage(HttpClientErrorException e) {
        try {
            byte[] responseBody = e.getResponseBodyAsByteArray();
            if (responseBody != null && responseBody.length > 0) {
                String body = new String(responseBody);
                int msgStart = body.indexOf("\"message\":\"");
                if (msgStart >= 0) {
                    int valueStart = msgStart + 11;
                    int valueEnd = body.indexOf('"', valueStart);
                    if (valueEnd > valueStart) {
                        String message = body.substring(valueStart, valueEnd);
                        log.error("OpenRouter error: {}", message);
                        return message;
                    }
                }
                log.error("OpenRouter error body: {}", body);
            }
        } catch (Exception ex) {
            log.error("Failed to parse OpenRouter error response", ex);
        }
        return null;
    }

}

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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OpenRouterClient implements AiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);
    private static final String ROLE_KEY = "role";
    private static final String CONTENT_KEY = "content";

    private final RestClient restClient;
    private final List<String> allModels;
    private final AtomicReference<String> currentModel;

    public OpenRouterClient(OpenRouterProperties properties) {
        String apiKey = properties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiServiceException(
                    "OpenRouter API key is not configured. "
                    + "Add OPENROUTER_API_KEY to the .env file in the backend directory.");
        }

        List<String> models = new ArrayList<>();
        models.add(properties.getModel());
        List<String> fallbacks = properties.getFallbackModels();
        if (fallbacks != null) {
            models.addAll(fallbacks);
        }
        this.allModels = List.copyOf(models);
        this.currentModel = new AtomicReference<>(properties.getModel());

        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        log.info("OpenRouter configured with primary model '{}' and {} fallback(s)",
                properties.getModel(), fallbacks != null ? fallbacks.size() : 0);
    }

    @Override
    public String sendMessage(List<ChatMessage> history, String systemPrompt) {
        String model = currentModel.get();
        String result = trySend(model, history, systemPrompt);
        if (result != null) {
            return result;
        }

        for (String fallback : allModels) {
            if (fallback.equals(model)) {
                continue;
            }
            log.warn("Falling back to model '{}'", fallback);
            result = trySend(fallback, history, systemPrompt);
            if (result != null) {
                currentModel.set(fallback);
                log.info("Switched sticky model to '{}'", fallback);
                return result;
            }
        }

        throw new AiServiceException(
                "All available models are currently rate-limited or unavailable. Please try again later.");
    }

    private String trySend(String model, List<ChatMessage> history, String systemPrompt) {
        try {
            return sendWithModel(model, history, systemPrompt);
        } catch (AiServiceException e) {
            log.warn("Model '{}' failed: {}", model, e.getMessage());
            return null;
        }
    }

    private String sendWithModel(String model, List<ChatMessage> history, String systemPrompt) {
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
            if (isNonRetryable(e.getStatusCode())) {
                throw new AiServiceException(mapClientError(e));
            }
            throw new AiServiceException(mapClientError(e));
        } catch (HttpServerErrorException e) {
            log.warn("OpenRouter server error for model '{}': {}", model, e.getMessage());
            throw new AiServiceException("Model '" + model + "' is temporarily unavailable.");
        } catch (ResourceAccessException e) {
            log.warn("Connection error for model '{}': {}", model, e.getMessage());
            throw new AiServiceException("Model '" + model + "' is unreachable.");
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

        log.info("OpenRouter response received from '{}', {} chars", model, content.length());
        return content;
    }

    private boolean isNonRetryable(HttpStatusCode status) {
        return status.value() == 401 || status.value() == 402 || status.value() == 403;
    }

    private String mapClientError(HttpClientErrorException e) {
        HttpStatusCode status = e.getStatusCode();
        String openRouterMessage = extractOpenRouterErrorMessage(e);

        return switch (status.value()) {
            case 401 -> "Authentication failed. Please verify your OpenRouter API key.";
            case 402 -> openRouterMessage != null
                    ? openRouterMessage
                    : "Insufficient credits on your OpenRouter account.";
            case 403 -> "Access denied. Your API key may not have access to this model.";
            case 429 -> openRouterMessage != null
                    ? openRouterMessage
                    : "Rate limit exceeded for model.";
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

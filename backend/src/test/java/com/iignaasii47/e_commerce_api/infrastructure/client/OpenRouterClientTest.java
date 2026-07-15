package com.iignaasii47.e_commerce_api.infrastructure.client;

import com.iignaasii47.e_commerce_api.domain.exception.AiServiceException;
import com.iignaasii47.e_commerce_api.domain.model.ChatAiResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OpenRouterClientTest {

    private static final List<ChatMessage> SINGLE_USER_MSG = List.of(ChatMessage.user("Hi"));
    private static final String SYSTEM_PROMPT = "You are helpful";

    private MockRestServiceServer mockServer;
    private OpenRouterClient client;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        restClient = builder.build();
        client = newClient(restClient, "model-a", "model-b");
    }

    private OpenRouterClient newClient(RestClient restClient, String model, String... fallbacks) {
        OpenRouterProperties props = new OpenRouterProperties();
        props.setApiKey("test-key");
        props.setApiUrl("http://localhost:8080");
        props.setModel(model);
        props.setFallbackModels(List.of(fallbacks));
        OpenRouterClient c = new OpenRouterClient(props);
        c.restClient = restClient;
        return c;
    }

    @Test
    void shouldSendMessageAndReturnTextResponse() {
        mockServer.expect(requestTo("/chat/completions"))
                .andRespond(withSuccess("""
                        {
                            "choices": [{
                                "message": { "content": "Hello, how can I help you?" }
                            }]
                        }
                        """, MediaType.APPLICATION_JSON));

        ChatAiResponse response = client.sendMessage(SINGLE_USER_MSG, SYSTEM_PROMPT, List.of());

        assertThat(response.hasToolCalls()).isFalse();
        assertThat(response.getContent()).isEqualTo("Hello, how can I help you?");
    }

    @Test
    void shouldParseToolCalls() {
        mockServer.expect(requestTo("/chat/completions"))
                .andRespond(withSuccess("""
                        {
                            "choices": [{
                                "message": {
                                    "tool_calls": [{
                                        "id": "call_1",
                                        "function": {
                                            "name": "search_products",
                                            "arguments": "{\\"query\\":\\"keyboard\\"}"
                                        }
                                    }]
                                }
                            }]
                        }
                        """, MediaType.APPLICATION_JSON));

        ChatAiResponse response = client.sendMessage(
                List.of(ChatMessage.user("search")), "prompt",
                List.of(Map.of("type", "function")));

        assertThat(response.hasToolCalls()).isTrue();
        assertThat(response.getToolCalls()).hasSize(1);
        assertThat(response.getToolCalls().get(0).getFunctionName()).isEqualTo("search_products");
    }

    @Test
    void shouldFallbackToNextModelOnError() {
        mockServer.expect(requestTo("/chat/completions"))
                .andRespond(withServerError());
        mockServer.expect(requestTo("/chat/completions"))
                .andRespond(withSuccess("""
                        {
                            "choices": [{
                                "message": { "content": "fallback response" }
                            }]
                        }
                        """, MediaType.APPLICATION_JSON));

        ChatAiResponse response = client.sendMessage(SINGLE_USER_MSG, "prompt", List.of());

        assertThat(response.getContent()).isEqualTo("fallback response");
    }

    @Test
    void shouldThrowWhenAllModelsFail() {
        mockServer.expect(requestTo("/chat/completions")).andRespond(withServerError());
        mockServer.expect(requestTo("/chat/completions")).andRespond(withServerError());

        assertThatThrownBy(() -> client.sendMessage(SINGLE_USER_MSG, "prompt", List.of()))
                .isInstanceOf(AiServiceException.class)
                .hasMessageContaining("All available models");
    }

    @ParameterizedTest
    @MethodSource("malformedResponses")
    void shouldThrowOnMalformedResponse(String responseJson) {
        client = newClient(restClient, "only-model");
        mockServer.expect(requestTo("/chat/completions"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.sendMessage(SINGLE_USER_MSG, "prompt", List.of()))
                .isInstanceOf(AiServiceException.class)
                .hasMessageContaining("All available models");
    }

    static Stream<Arguments> malformedResponses() {
        return Stream.of(
                Arguments.of("""
                        { "choices": [] }
                        """),
                Arguments.of("""
                        {
                            "choices": [{ "message": null }]
                        }
                        """),
                Arguments.of("""
                        {
                            "choices": [{ "message": { "content": null } }]
                        }
                        """)
        );
    }

    @Test
    void shouldSendSystemPromptAndHistory() {
        mockServer.expect(requestTo("/chat/completions"))
                .andRespond(withSuccess("""
                        {
                            "choices": [{ "message": { "content": "ok" } }]
                        }
                        """, MediaType.APPLICATION_JSON));

        client.sendMessage(
                List.of(ChatMessage.user("Hello"), ChatMessage.assistant("Hi there")),
                "System prompt", List.of());

        mockServer.verify();
    }
}

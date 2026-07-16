package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;
import com.iignaasii47.e_commerce_api.domain.model.ChatResult;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatUseCase chatUseCase;

    @MockitoBean
    private TokenService tokenService;

    private static final UsernamePasswordAuthenticationToken AUTH =
            new UsernamePasswordAuthenticationToken(1L, null, List.of());

    @Test
    void shouldReturnReplyWhenValidRequest() throws Exception {
        when(chatUseCase.chat(anyString(), anyList()))
                .thenReturn(new ChatResult("AI response text", List.of()));

        String requestBody = """
                {
                    "message": "hello",
                    "history": []
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("AI response text"))
                .andExpect(jsonPath("$.toolsUsed").isEmpty());
    }

    @Test
    void shouldReturnToolsUsed() throws Exception {
        when(chatUseCase.chat(anyString(), anyList()))
                .thenReturn(new ChatResult("Done", List.of("search_products", "add_to_cart")));

        String requestBody = """
                {
                    "message": "add keyboard to cart",
                    "history": []
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Done"))
                .andExpect(jsonPath("$.toolsUsed[0]").value("search_products"))
                .andExpect(jsonPath("$.toolsUsed[1]").value("add_to_cart"));
    }

    @Test
    void shouldReturn400WhenMessageIsBlank() throws Exception {
        String requestBody = """
                {
                    "message": "",
                    "history": []
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenMessageIsMissing() throws Exception {
        String requestBody = """
                {
                    "history": []
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WithEmptyHistoryWhenNotProvided() throws Exception {
        when(chatUseCase.chat(anyString(), anyList()))
                .thenReturn(ChatResult.of("response"));

        String requestBody = """
                {
                    "message": "hello"
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("response"))
                .andExpect(jsonPath("$.toolsUsed").isEmpty());
    }
}

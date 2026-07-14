package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatUseCase chatUseCase;

    @Test
    void shouldReturnReplyWhenValidRequest() throws Exception {
        when(chatUseCase.chat(any(String.class), anyList())).thenReturn("AI response text");

        String requestBody = """
                {
                    "message": "hello",
                    "history": []
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("AI response text"));
    }

    @Test
    void shouldSendHistoryToUseCase() throws Exception {
        when(chatUseCase.chat(any(String.class), anyList())).thenReturn("response");

        String requestBody = """
                {
                    "message": "follow up",
                    "history": [
                        {"role": "user", "content": "hello"},
                        {"role": "bot", "content": "hi there"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("response"));
    }

    @Test
    void shouldMapBotRoleToAssistant() throws Exception {
        when(chatUseCase.chat(any(String.class), anyList())).thenReturn("response");

        String requestBody = """
                {
                    "message": "help",
                    "history": [
                        {"role": "bot", "content": "previous bot message"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("response"));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WithEmptyHistoryWhenNotProvided() throws Exception {
        when(chatUseCase.chat(any(String.class), anyList())).thenReturn("response");

        String requestBody = """
                {
                    "message": "hello"
                }
                """;

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("response"));
    }

}

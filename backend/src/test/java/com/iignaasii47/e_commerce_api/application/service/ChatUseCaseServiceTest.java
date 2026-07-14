package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;
import com.iignaasii47.e_commerce_api.domain.port.out.CvDataProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatUseCaseServiceTest {

    @Mock
    private AiClient aiClient;

    @Mock
    private CvDataProvider cvDataProvider;

    @InjectMocks
    private ChatUseCaseService chatUseCaseService;

    @Test
    void shouldSendMessageWithSystemPromptAndCvData() {
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(any(), anyString())).thenReturn("AI response");

        String response = chatUseCaseService.chat("hello", List.of());

        assertThat(response).isEqualTo("AI response");
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiClient).sendMessage(any(), promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("term-shop", "CV content");
    }

    @Test
    void shouldIncludeHistoryInFullHistory() {
        ChatMessage previous = ChatMessage.assistant("previous reply");
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(any(), anyString())).thenReturn("response");

        chatUseCaseService.chat("new message", List.of(previous));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ChatMessage>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(aiClient).sendMessage(historyCaptor.capture(), anyString());
        List<ChatMessage> fullHistory = historyCaptor.getValue();
        assertThat(fullHistory).hasSize(2);
        assertThat(fullHistory.get(0)).isEqualTo(previous);
        assertThat(fullHistory.get(1).getRole()).isEqualTo("user");
        assertThat(fullHistory.get(1).getContent()).isEqualTo("new message");
    }

    @Test
    void shouldAddUserMessageToEmptyHistory() {
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(any(), anyString())).thenReturn("response");

        chatUseCaseService.chat("first message", List.of());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ChatMessage>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(aiClient).sendMessage(historyCaptor.capture(), anyString());
        List<ChatMessage> fullHistory = historyCaptor.getValue();
        assertThat(fullHistory).hasSize(1);
        assertThat(fullHistory.get(0).getRole()).isEqualTo("user");
        assertThat(fullHistory.get(0).getContent()).isEqualTo("first message");
    }

    @Test
    void shouldPreserveOriginalHistoryImmutability() {
        ChatMessage previous = ChatMessage.assistant("previous");
        List<ChatMessage> originalHistory = List.of(previous);
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(any(), anyString())).thenReturn("response");

        chatUseCaseService.chat("message", originalHistory);

        assertThat(originalHistory).hasSize(1);
    }

    @Test
    void shouldIncludeAppDescriptionInSystemPrompt() {
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(any(), anyString())).thenReturn("response");

        chatUseCaseService.chat("hello", List.of());

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiClient).sendMessage(any(), promptCaptor.capture());
        String prompt = promptCaptor.getValue();
        assertThat(prompt).contains(
                "term-shop",
                "Spring Boot 4.1",
                "Angular 22",
                "PostgreSQL",
                "JWT"
        );
    }

}

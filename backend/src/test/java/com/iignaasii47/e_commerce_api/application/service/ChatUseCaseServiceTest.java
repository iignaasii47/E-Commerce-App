package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.domain.model.ChatAiResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.model.ChatResult;
import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;
import com.iignaasii47.e_commerce_api.domain.port.out.ChatToolExecutor;
import com.iignaasii47.e_commerce_api.domain.port.out.CvDataProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatUseCaseServiceTest {

    @Mock
    private AiClient aiClient;

    @Mock
    private CvDataProvider cvDataProvider;

    @Mock
    private ChatToolExecutor chatToolExecutor;

    @InjectMocks
    private ChatUseCaseService chatUseCaseService;

    @Test
    void shouldSendMessageWithSystemPromptAndCvData() {
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(anyList(), anyString(), anyList()))
                .thenReturn(ChatAiResponse.text("AI response"));

        ChatResult result = chatUseCaseService.chat("hello", List.of(), 1L);

        assertThat(result.getReply()).isEqualTo("AI response");
        assertThat(result.getToolsUsed()).isEmpty();
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiClient).sendMessage(anyList(), promptCaptor.capture(), anyList());
        assertThat(promptCaptor.getValue()).contains("term-shop", "CV content");
    }

    @Test
    void shouldIncludeHistoryInFullHistory() {
        ChatMessage previous = ChatMessage.assistant("previous reply");
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(anyList(), anyString(), anyList()))
                .thenReturn(ChatAiResponse.text("response"));

        chatUseCaseService.chat("new message", List.of(previous), 1L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ChatMessage>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(aiClient).sendMessage(historyCaptor.capture(), anyString(), anyList());
        List<ChatMessage> fullHistory = historyCaptor.getValue();
        assertThat(fullHistory).hasSize(2);
        assertThat(fullHistory.get(0)).isEqualTo(previous);
        assertThat(fullHistory.get(1).getRole()).isEqualTo("user");
        assertThat(fullHistory.get(1).getContent()).isEqualTo("new message");
    }

    @Test
    void shouldReturnEmptyToolsUsedWhenNoTools() {
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(anyList(), anyString(), anyList()))
                .thenReturn(ChatAiResponse.text("response"));

        ChatResult result = chatUseCaseService.chat("hello", List.of(), 1L);

        assertThat(result.getToolsUsed()).isEmpty();
    }

    @Test
    void shouldPreserveOriginalHistoryImmutability() {
        ChatMessage previous = ChatMessage.assistant("previous");
        List<ChatMessage> originalHistory = List.of(previous);
        when(cvDataProvider.getCvContent()).thenReturn("CV content");
        when(aiClient.sendMessage(anyList(), anyString(), anyList()))
                .thenReturn(ChatAiResponse.text("response"));

        chatUseCaseService.chat("message", originalHistory, 1L);

        assertThat(originalHistory).hasSize(1);
    }

}

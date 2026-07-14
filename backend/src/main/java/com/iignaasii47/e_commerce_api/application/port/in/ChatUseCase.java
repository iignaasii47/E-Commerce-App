package com.iignaasii47.e_commerce_api.application.port.in;

import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;

import java.util.List;

public interface ChatUseCase {

    String chat(String userMessage, List<ChatMessage> history, Long userId);

}

package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;

import java.util.List;

public interface AiClient {

    String sendMessage(List<ChatMessage> history, String systemPrompt);

}

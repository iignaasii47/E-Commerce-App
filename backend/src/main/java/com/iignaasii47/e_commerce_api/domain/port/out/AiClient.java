package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.ChatAiResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;

import java.util.List;
import java.util.Map;

public interface AiClient {

    ChatAiResponse sendMessage(List<ChatMessage> history, String systemPrompt, List<Map<String, Object>> tools);

}

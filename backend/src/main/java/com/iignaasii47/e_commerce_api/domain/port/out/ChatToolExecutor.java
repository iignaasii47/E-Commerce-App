package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.ChatToolCall;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolResult;

public interface ChatToolExecutor {

    ChatToolResult execute(ChatToolCall toolCall, Long userId);

}

package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single chat message in the conversation history")
public record MessageDto(
        @Schema(description = "Role of the message sender (user, assistant, or bot)", example = "user")
        String role,
        @Schema(description = "Message content", example = "Show me keyboards under $100")
        String content
) {
}

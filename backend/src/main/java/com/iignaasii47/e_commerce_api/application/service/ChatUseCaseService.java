package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;
import com.iignaasii47.e_commerce_api.domain.model.ChatAiResponse;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.model.ChatResult;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolCall;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolResult;
import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;
import com.iignaasii47.e_commerce_api.domain.port.out.ChatToolExecutor;
import com.iignaasii47.e_commerce_api.domain.port.out.CvDataProvider;
import com.iignaasii47.e_commerce_api.domain.port.out.SecurityContextProvider;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatUseCaseService implements ChatUseCase {

    private static final int MAX_TOOL_ITERATIONS = 5;

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_TYPE_INTEGER = "integer";

    private static final String APP_DESCRIPTION = """
            You are a helpful assistant for a terminal-themed e-commerce web application called "term-shop".
            
            You can help users browse products, manage their shopping cart, and answer questions about the application.
            Use the available tools to search products, add items to the cart, remove items, and view the cart.
            Always confirm actions to the user after using tools.
            """;

    private static final String CV_INSTRUCTIONS = """
            Below is the CV of the application's developer. You can answer questions about their background,
            skills, experience, and projects. If asked about the developer, refer to this CV.
            
            """;

    private static final List<Map<String, Object>> TOOLS = List.of(
            toolDef("search_products", "Search for products by name or category",
                    Map.of("query", Map.of("type", "string", KEY_DESCRIPTION, "Search query")),
                    List.of("query")),
            toolDef("get_product", "Get details about a specific product by ID",
                    Map.of(KEY_PRODUCT_ID, Map.of("type", KEY_TYPE_INTEGER, KEY_DESCRIPTION, "Product ID")),
                    List.of(KEY_PRODUCT_ID)),
            toolDef("list_categories", "List all available product categories",
                    Map.of(),
                    List.of()),
            toolDef("add_to_cart", "Add a product to the user's shopping cart",
                    Map.of(
                            KEY_PRODUCT_ID, Map.of("type", KEY_TYPE_INTEGER, KEY_DESCRIPTION, "Product ID"),
                            "quantity", Map.of("type", KEY_TYPE_INTEGER, KEY_DESCRIPTION, "Quantity to add (default 1)")
                    ),
                    List.of(KEY_PRODUCT_ID)),
            toolDef("remove_from_cart", "Remove a product from the user's shopping cart by cart item ID",
                    Map.of("cart_item_id", Map.of("type", KEY_TYPE_INTEGER, KEY_DESCRIPTION, "Cart item ID to remove")),
                    List.of("cart_item_id")),
            toolDef("view_cart", "Show the current contents of the user's shopping cart",
                    Map.of(),
                    List.of())
    );

    private final AiClient aiClient;
    private final CvDataProvider cvDataProvider;
    private final ChatToolExecutor chatToolExecutor;
    private final SecurityContextProvider securityContextProvider;

    public ChatUseCaseService(AiClient aiClient, CvDataProvider cvDataProvider,
                               ChatToolExecutor chatToolExecutor,
                               SecurityContextProvider securityContextProvider) {
        this.aiClient = aiClient;
        this.cvDataProvider = cvDataProvider;
        this.chatToolExecutor = chatToolExecutor;
        this.securityContextProvider = securityContextProvider;
    }

    @Override
    public ChatResult chat(String userMessage, List<ChatMessage> history) {
        Long userId = securityContextProvider.getCurrentUserId();
        String systemPrompt = APP_DESCRIPTION + CV_INSTRUCTIONS + cvDataProvider.getCvContent();

        List<ChatMessage> messages = new ArrayList<>(history);
        messages.add(ChatMessage.user(userMessage));
        List<String> toolsUsed = new ArrayList<>();

        for (int i = 0; i < MAX_TOOL_ITERATIONS; i++) {
            ChatAiResponse response = aiClient.sendMessage(messages, systemPrompt, TOOLS);

            if (response.hasToolCalls()) {
                for (ChatToolCall toolCall : response.getToolCalls()) {
                    toolsUsed.add(toolCall.getFunctionName());
                    ChatToolResult result = chatToolExecutor.execute(toolCall, userId);
                    messages.add(ChatMessage.tool(result.getToolCallId(), result.getResult()));
                }
                continue;
            }

            String reply = response.getContent() != null
                    ? response.getContent()
                    : "I'm not sure how to help with that.";
            return new ChatResult(reply, toolsUsed);
        }

        return new ChatResult(
                "I tried several times but couldn't complete your request. Please try again.",
                toolsUsed);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toolDef(String name, String description,
                                                Map<String, Object> properties, List<String> required) {
        return Map.of(
                "type", "function",
                "function", Map.of(
            "name", name,
                    KEY_DESCRIPTION, description,
                    "parameters", Map.of(
                                "type", "object",
                                "properties", properties,
                                "required", required
                        )
                )
        );
    }

}

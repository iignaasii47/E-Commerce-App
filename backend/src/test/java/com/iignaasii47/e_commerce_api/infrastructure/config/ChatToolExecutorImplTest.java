package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolCall;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolResult;
import com.iignaasii47.e_commerce_api.domain.model.Product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatToolExecutorImplTest {

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private CartUseCase cartUseCase;

    @InjectMocks
    private ChatToolExecutorImpl executor;

    @Test
    void shouldExecuteSearchProducts() {
        Product product = Product.builder()
                .id(1L).name("Keyboard").price(new BigDecimal("149.99"))
                .category("peripherals").stock(10).build();
        when(productUseCase.searchProducts("keyboard")).thenReturn(List.of(product));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "search_products", Map.of("query", "keyboard")), null);

        assertThat(result.getToolName()).isEqualTo("search_products");
        assertThat(result.getToolCallId()).isEqualTo("call_1");
        assertThat(result.getResult()).contains("[ID: 1]").contains("Keyboard").contains("$149.99");
    }

    @Test
    void shouldReturnNoProductsFound() {
        when(productUseCase.searchProducts("nonexistent")).thenReturn(List.of());

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "search_products", Map.of("query", "nonexistent")), null);

        assertThat(result.getResult()).isEqualTo("No products found.");
    }

    @Test
    void shouldExecuteGetProduct() {
        Product product = Product.builder()
                .id(1L).name("Mouse").price(new BigDecimal("79.99"))
                .category("peripherals").stock(5).build();
        when(productUseCase.getProductById(1L)).thenReturn(Optional.of(product));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "get_product", Map.of("product_id", 1)), null);

        assertThat(result.getResult()).contains("Mouse").contains("$79.99");
    }

    @Test
    void shouldReturnProductNotFound() {
        when(productUseCase.getProductById(99L)).thenReturn(Optional.empty());

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "get_product", Map.of("product_id", 99)), null);

        assertThat(result.getResult()).isEqualTo("Product not found.");
    }

    @Test
    void shouldExecuteListCategories() {
        when(productUseCase.getCategories()).thenReturn(List.of("peripherals", "audio"));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "list_categories", Map.of()), null);

        assertThat(result.getResult()).isEqualTo("Categories: peripherals, audio");
    }

    @Test
    void shouldExecuteAddToCart() {
        CartItem item = new CartItem(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 3);
        when(cartUseCase.addToCart(10L, 5L, 3)).thenReturn(item);

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "add_to_cart",
                        Map.of("product_id", 5, "quantity", 3)), 10L);

        assertThat(result.getResult()).contains("Added Keyboard (x3)").contains("Cart item ID: 1");
    }

    @Test
    void shouldAddToCartWithDefaultQuantity() {
        CartItem item = new CartItem(1L, 10L, 5L, "Mouse", new BigDecimal("29.99"), 1);
        when(cartUseCase.addToCart(10L, 5L, 1)).thenReturn(item);

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "add_to_cart",
                        Map.of("product_id", 5)), 10L);

        assertThat(result.getResult()).contains("Added Mouse (x1)");
    }

    @Test
    void shouldExecuteRemoveFromCart() {
        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "remove_from_cart",
                        Map.of("cart_item_id", 3)), 10L);

        assertThat(result.getResult()).contains("Removed cart item 3");
    }

    @Test
    void shouldExecuteViewCart() {
        CartItem item = new CartItem(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        when(cartUseCase.getCart(10L)).thenReturn(List.of(item));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "view_cart", Map.of()), 10L);

        assertThat(result.getResult()).contains("Your cart:").contains("Keyboard x2");
    }

    @Test
    void shouldExecuteViewCartEmpty() {
        when(cartUseCase.getCart(10L)).thenReturn(List.of());

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "view_cart", Map.of()), 10L);

        assertThat(result.getResult()).isEqualTo("Your cart is empty.");
    }

    @Test
    void shouldHandleUnknownTool() {
        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "unknown_tool", Map.of()), null);

        assertThat(result.getResult()).isEqualTo("Unknown tool: unknown_tool");
    }

    @Test
    void shouldReturnErrorWhenToolThrows() {
        when(productUseCase.searchProducts("fail")).thenThrow(new RuntimeException("DB down"));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "search_products", Map.of("query", "fail")), null);

        assertThat(result.getResult()).startsWith("Error: DB down");
    }

    @Test
    void shouldConvertProductIdLong() {
        Product product = Product.builder()
                .id(42L).name("Test").price(BigDecimal.ONE).category("cat").stock(1).build();
        when(productUseCase.getProductById(42L)).thenReturn(Optional.of(product));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "get_product", Map.of("product_id", 42L)), null);

        assertThat(result.getResult()).contains("Test");
    }

    @Test
    void shouldFormatMultipleProducts() {
        Product p1 = Product.builder().id(1L).name("A").price(BigDecimal.ONE).category("cat").stock(1).build();
        Product p2 = Product.builder().id(2L).name("B").price(BigDecimal.TEN).category("cat2").stock(2).build();
        when(productUseCase.searchProducts("test")).thenReturn(List.of(p1, p2));

        ChatToolResult result = executor.execute(
                new ChatToolCall("call_1", "search_products", Map.of("query", "test")), null);

        assertThat(result.getResult()).contains("[ID: 1] A").contains("[ID: 2] B");
    }
}

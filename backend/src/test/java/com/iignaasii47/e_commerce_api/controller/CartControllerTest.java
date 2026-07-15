package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartUseCase cartUseCase;

    @MockitoBean
    private UserIdExtractor userIdExtractor;

    private static final String AUTH_HEADER = "Bearer valid-token";

    @Test
    void shouldReturnEmptyCart() throws Exception {
        when(userIdExtractor.extract(AUTH_HEADER)).thenReturn(1L);
        when(cartUseCase.getCart(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/cart").header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnCartWithItems() throws Exception {
        CartItem item = new CartItem(1L, 1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        when(userIdExtractor.extract(AUTH_HEADER)).thenReturn(1L);
        when(cartUseCase.getCart(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/cart").header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productId").value(5))
                .andExpect(jsonPath("$[0].productName").value("Keyboard"))
                .andExpect(jsonPath("$[0].unitPrice").value(149.99))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].subtotal").value(299.98));
    }

    @Test
    void shouldAddItemToCart() throws Exception {
        CartItem item = new CartItem(1L, 1L, 5L, "Keyboard", new BigDecimal("149.99"), 1);
        when(userIdExtractor.extract(AUTH_HEADER)).thenReturn(1L);
        when(cartUseCase.addToCart(1L, 5L, 2)).thenReturn(item);

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", AUTH_HEADER)
                        .param("productId", "5")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Keyboard"))
                .andExpect(jsonPath("$.quantity").value(1));
    }

    @Test
    void shouldAddItemWithDefaultQuantity() throws Exception {
        CartItem item = new CartItem(1L, 1L, 5L, "Mouse", new BigDecimal("29.99"), 1);
        when(userIdExtractor.extract(AUTH_HEADER)).thenReturn(1L);
        when(cartUseCase.addToCart(1L, 5L, 1)).thenReturn(item);

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", AUTH_HEADER)
                        .param("productId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Mouse"));
    }

    @Test
    void shouldRemoveItemFromCart() throws Exception {
        when(userIdExtractor.extract(AUTH_HEADER)).thenReturn(1L);
        doNothing().when(cartUseCase).removeFromCart(1L, 7L);

        mockMvc.perform(delete("/api/cart/7")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("removed"));
    }
}

package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.OrderUseCase;
import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @MockitoBean
    private TokenService tokenService;

    private static final UsernamePasswordAuthenticationToken AUTH =
            new UsernamePasswordAuthenticationToken(1L, null, List.of());

    private static final LocalDateTime NOW = LocalDateTime.of(2026, Month.JULY, 21, 12, 0);

    @Test
    void shouldCreateOrder() throws Exception {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        Order order = Order.builder()
                .id(1L).userId(1L).status(OrderStatus.CONFIRMED)
                .items(List.of(item)).total(new BigDecimal("299.98"))
                .shippingAddress("123 Main St").shippingCity("Springfield").shippingZip("12345")
                .createdAt(NOW).build();

        when(orderUseCase.createOrder("123 Main St", "Springfield", "12345")).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"shippingAddress":"123 Main St","shippingCity":"Springfield","shippingZip":"12345"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.total").value(299.98))
                .andExpect(jsonPath("$.items[0].productName").value("Keyboard"));
    }

    @Test
    void shouldReturn400WhenRequestBodyInvalid() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOrder() throws Exception {
        OrderItem item = new OrderItem(1L, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        Order order = Order.builder()
                .id(1L).userId(1L).status(OrderStatus.CONFIRMED)
                .items(List.of(item)).total(new BigDecimal("299.98"))
                .shippingAddress("123 Main St").shippingCity("Springfield").shippingZip("12345")
                .createdAt(NOW).build();

        when(orderUseCase.getOrderById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/1")
                        .with(authentication(AUTH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.shippingAddress").value("123 Main St"));
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(orderUseCase.getOrderById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/99")
                        .with(authentication(AUTH)))
                .andExpect(status().isNotFound());
    }

}

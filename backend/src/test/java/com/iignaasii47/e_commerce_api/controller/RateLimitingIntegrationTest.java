package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiClient aiClient;

    @Test
    void shouldReturn429OnSixthLoginAttempt() throws Exception {
        String body = "{\"email\":\"test@example.com\",\"password\":\"wrongpass\"}";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(not(429)));
        }

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(429))
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    void shouldReturn429OnFourthRegistrationAttempt() throws Exception {
        String body = "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"secret123\"}";

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().is(not(429)));
        }

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(429))
                .andExpect(header().exists("Retry-After"));
    }

}

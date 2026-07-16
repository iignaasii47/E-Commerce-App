package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;
import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void shouldRegisterUserAndReturn201() throws Exception {
        User savedUser = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        when(userUseCase.register(any(User.class))).thenReturn(savedUser);

        String requestBody = """
                {
                    "username": "john",
                    "email": "john@example.com",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @ParameterizedTest
    @CsvSource({
            "'', john@example.com, secret123",
            "john, john@example.com, short",
            "john, not-an-email, secret123"
    })
    void shouldReturn400WhenRequestInvalid(String username, String email, String password) throws Exception {
        String requestBody = """
                {
                    "username": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(username, email, password);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenDuplicateUser() throws Exception {
        when(userUseCase.register(any(User.class)))
                .thenThrow(new DuplicateUserException("Username 'john' is already taken"));

        String requestBody = """
                {
                    "username": "john",
                    "email": "john@example.com",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username 'john' is already taken"));
    }

    @Test
    void shouldLoginAndReturn200WithToken() throws Exception {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        Authentication auth = new Authentication(user, "jwt-token-value");
        when(userUseCase.login("john@example.com", "secret123")).thenReturn(auth);

        String requestBody = """
                {
                    "email": "john@example.com",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.token").value("jwt-token-value"));
    }

    @Test
    void shouldReturn401WhenInvalidCredentials() throws Exception {
        when(userUseCase.login("john@example.com", "wrong")).thenThrow(new InvalidCredentialsException("Invalid email or password"));

        String requestBody = """
                {
                    "email": "john@example.com",
                    "password": "wrong"
                }
                """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldReturn400WhenLoginRequestMissingFields() throws Exception {
        String requestBody = """
                {
                    "username": "",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

}

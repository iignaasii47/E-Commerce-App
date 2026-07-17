package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.CreateUserRequest;
import com.iignaasii47.e_commerce_api.controller.dto.LoginRequest;
import com.iignaasii47.e_commerce_api.controller.dto.LoginResponse;
import com.iignaasii47.e_commerce_api.controller.dto.UserResponse;
import com.iignaasii47.e_commerce_api.domain.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Auth", description = "User registration and authentication")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user",
            description = "Creates a new user account. Username and email must be unique.")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Validation error in request body",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"email: must be a well-formed email address\"}")))
    @ApiResponse(responseCode = "409", description = "Username or email already taken",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":409,\"error\":\"Conflict\",\"message\":\"Username 'john_doe' is already taken\"}")))
    public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
        User user = new User(null, request.username(), request.email(), request.password(), null);
        User created = userUseCase.register(user);
        return UserResponse.from(created);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token",
            description = "Authenticates with email and password. Returns a JWT token to use in the Authorization header for protected endpoints. "
                    + "The token expires after 24 hours.")
    @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned")
    @ApiResponse(responseCode = "400", description = "Validation error in request body",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"email: must not be blank\"}")))
    @ApiResponse(responseCode = "401", description = "Invalid email or password",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid email or password\"}")))
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return LoginResponse.from(userUseCase.login(request.email(), request.password()));
    }

}

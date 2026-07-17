package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.LoginResponse;
import com.iignaasii47.e_commerce_api.controller.dto.RefreshTokenRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Token management (refresh and logout)")
public class AuthController {

    private final UserUseCase userUseCase;

    public AuthController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
            description = "Exchanges a valid refresh token for a new access token and refresh token pair. "
                    + "The old refresh token is invalidated (single-use rotation).")
    @ApiResponse(responseCode = "200", description = "New token pair returned")
    @ApiResponse(responseCode = "400", description = "Validation error in request body",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"refreshToken: must not be blank\"}")))
    @ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Refresh token has expired\"}")))
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return LoginResponse.from(userUseCase.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate refresh token",
            description = "Invalidates the provided refresh token so it can no longer be used.")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @ApiResponse(responseCode = "400", description = "Validation error in request body",
            content = @Content(schema = @Schema(example = "{\"timestamp\":\"2026-07-16T10:30:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"refreshToken: must not be blank\"}")))
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        userUseCase.logout(request.refreshToken());
    }

}

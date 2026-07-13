package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.CreateUserRequest;
import com.iignaasii47.e_commerce_api.controller.dto.LoginRequest;
import com.iignaasii47.e_commerce_api.controller.dto.LoginResponse;
import com.iignaasii47.e_commerce_api.controller.dto.UserResponse;
import com.iignaasii47.e_commerce_api.domain.model.User;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
        User user = new User(null, request.getUsername(), request.getEmail(), request.getPassword(), null);
        User created = userUseCase.register(user);
        return UserResponse.from(created);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return LoginResponse.from(userUseCase.login(request.getUsername(), request.getPassword()));
    }

}

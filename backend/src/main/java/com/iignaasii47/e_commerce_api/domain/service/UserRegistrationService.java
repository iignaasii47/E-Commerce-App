package com.iignaasii47.e_commerce_api.domain.service;

import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;

public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("Username '" + user.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Email '" + user.getEmail() + "' is already registered");
        }
    }

}

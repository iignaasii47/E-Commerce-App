package com.iignaasii47.e_commerce_api.domain.service;

import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.exception.WeakPasswordException;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;

public class UserRegistrationService {

    private final UserRepository userRepository;
    private final int passwordMinLength;
    private final boolean passwordRequireMixed;

    public UserRegistrationService(UserRepository userRepository, int passwordMinLength, boolean passwordRequireMixed) {
        this.userRepository = userRepository;
        this.passwordMinLength = passwordMinLength;
        this.passwordRequireMixed = passwordRequireMixed;
    }

    public void validate(User user) {
        validatePassword(user.getPassword());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("Username '" + user.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Email '" + user.getEmail() + "' is already registered");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < passwordMinLength) {
            throw new WeakPasswordException("Password must be at least " + passwordMinLength + " characters long");
        }
        if (passwordRequireMixed) {
            if (password.matches("[0-9]+")) {
                throw new WeakPasswordException("Password must not be purely numeric");
            }
            if (password.matches("[a-zA-Z]+")) {
                throw new WeakPasswordException("Password must not be purely alphabetic");
            }
        }
    }

}

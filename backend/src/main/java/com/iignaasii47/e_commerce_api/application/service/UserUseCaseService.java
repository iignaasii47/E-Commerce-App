package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;
import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.PasswordEncryption;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUseCaseService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryption passwordEncryption;
    private final UserRegistrationService userRegistrationService;
    private final TokenService tokenService;

    public UserUseCaseService(UserRepository userRepository,
                               PasswordEncryption passwordEncryption,
                               UserRegistrationService userRegistrationService,
                               TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncryption = passwordEncryption;
        this.userRegistrationService = userRegistrationService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public User register(User user) {
        String encryptedPassword = passwordEncryption.encrypt(user.getPassword());
        User userToRegister = new User(null, user.getUsername(), user.getEmail(), encryptedPassword, null);
        userRegistrationService.validate(userToRegister);
        return userRepository.save(userToRegister);
    }

    @Override
    @Transactional(readOnly = true)
    public Authentication login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncryption.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = tokenService.generateToken(user.getId(), user.getUsername());
        return new Authentication(user, token);
    }

}

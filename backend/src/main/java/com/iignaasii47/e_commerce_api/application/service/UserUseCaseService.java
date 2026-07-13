package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.PasswordEncryption;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserUseCaseService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryption passwordEncryption;
    private final UserRegistrationService userRegistrationService;

    public UserUseCaseService(UserRepository userRepository,
                               PasswordEncryption passwordEncryption,
                               UserRegistrationService userRegistrationService) {
        this.userRepository = userRepository;
        this.passwordEncryption = passwordEncryption;
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    @Transactional
    public User register(User user) {
        String encryptedPassword = passwordEncryption.encrypt(user.getPassword());
        User userToRegister = new User(null, user.getUsername(), user.getEmail(), encryptedPassword, null);
        userRegistrationService.validate(userToRegister);
        return userRepository.save(userToRegister);
    }

}

package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public UserRegistrationService userRegistrationService(
            UserRepository userRepository,
            @Value("${password.min-length:8}") int passwordMinLength,
            @Value("${password.require-mixed:true}") boolean requireMixed) {
        return new UserRegistrationService(userRepository, passwordMinLength, requireMixed);
    }

}

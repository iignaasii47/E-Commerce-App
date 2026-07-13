package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public UserRegistrationService userRegistrationService(UserRepository userRepository) {
        return new UserRegistrationService(userRepository);
    }

}

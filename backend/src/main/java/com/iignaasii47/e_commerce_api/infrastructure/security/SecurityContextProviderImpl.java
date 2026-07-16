package com.iignaasii47.e_commerce_api.infrastructure.security;

import com.iignaasii47.e_commerce_api.domain.port.out.SecurityContextProvider;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextProviderImpl implements SecurityContextProvider {

    @Override
    public Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return (Long) authentication.getPrincipal();
    }

}

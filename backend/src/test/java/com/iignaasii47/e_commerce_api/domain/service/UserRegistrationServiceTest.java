package com.iignaasii47.e_commerce_api.domain.service;

import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private final User user = new User(null, "john", "john@example.com", "pass", null);

    @Test
    void shouldPassWhenUsernameAndEmailAreUnique() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        assertThatCode(() -> userRegistrationService.validate(user)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenUsernameExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Username 'john' is already taken");
    }

    @Test
    void shouldThrowWhenEmailExists() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Email 'john@example.com' is already registered");
    }

}

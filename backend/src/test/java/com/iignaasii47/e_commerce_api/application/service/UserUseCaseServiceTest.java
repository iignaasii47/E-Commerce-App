package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;
import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.PasswordEncryption;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseServiceTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryption passwordEncryption;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserUseCaseService userUseCaseService;

    @Test
    void shouldEncryptPasswordAndSaveUser() {
        User input = new User(null, "john", "john@example.com", "rawPassword", null);
        User expectedSaved = new User(1L, "john", "john@example.com", "encryptedPassword", FIXED_TIME);

        when(passwordEncryption.encrypt("rawPassword")).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expectedSaved);

        User result = userUseCaseService.register(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("encryptedPassword");
        assertThat(result.getCreatedAt()).isEqualTo(FIXED_TIME);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encryptedPassword");

        verify(passwordEncryption).encrypt("rawPassword");
        verify(userRegistrationService).validate(any(User.class));
    }

    @Test
    void shouldReturnSavedUserWithGeneratedId() {
        User input = new User(null, "jane", "jane@example.com", "pwd", null);
        User expectedSaved = new User(42L, "jane", "jane@example.com", "encrypted", FIXED_TIME);

        when(passwordEncryption.encrypt("pwd")).thenReturn("encrypted");
        when(userRepository.save(any(User.class))).thenReturn(expectedSaved);

        User result = userUseCaseService.register(input);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getUsername()).isEqualTo("jane");
    }

    @Test
    void shouldLoginAndReturnToken() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncryption.matches("secret123", "encrypted")).thenReturn(true);
        when(tokenService.generateToken(1L, "john")).thenReturn("jwt-token");

        Authentication result = userUseCaseService.login("john", "secret123");

        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getUser().getUsername()).isEqualTo("john");
        assertThat(result.getToken()).isEqualTo("jwt-token");

        verify(tokenService).generateToken(1L, "john");
    }

    @Test
    void shouldThrowWhenUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCaseService.login("unknown", "pwd"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncryption.matches("wrong", "encrypted")).thenReturn(false);

        assertThatThrownBy(() -> userUseCaseService.login("john", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

}

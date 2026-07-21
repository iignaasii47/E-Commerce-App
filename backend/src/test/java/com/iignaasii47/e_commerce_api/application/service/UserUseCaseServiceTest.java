package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;
import com.iignaasii47.e_commerce_api.domain.exception.RefreshTokenException;
import com.iignaasii47.e_commerce_api.domain.exception.WeakPasswordException;
import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.RefreshToken;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.PasswordEncryption;
import com.iignaasii47.e_commerce_api.domain.port.out.RefreshTokenRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseServiceTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);
    private static final long REFRESH_EXPIRATION_MS = 604800000L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryption passwordEncryption;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private UserUseCaseService userUseCaseService;

    @BeforeEach
    void setup() {
        userUseCaseService = new UserUseCaseService(userRepository, passwordEncryption,
                userRegistrationService, tokenService, refreshTokenRepository, REFRESH_EXPIRATION_MS);
    }

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
        verify(userRegistrationService).validate(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("rawPassword");
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
    void shouldLoginAndReturnTokens() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncryption.matches("secret123", "encrypted")).thenReturn(true);
        when(tokenService.generateAccessToken(1L, "john@example.com")).thenReturn("access-jwt");
        when(tokenService.generateRefreshToken(1L, "john@example.com")).thenReturn("refresh-jwt");

        Authentication result = userUseCaseService.login("john@example.com", "secret123");

        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getUser().getUsername()).isEqualTo("john");
        assertThat(result.getAccessToken()).isEqualTo("access-jwt");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-jwt");

        verify(tokenService).generateAccessToken(1L, "john@example.com");
        verify(tokenService).generateRefreshToken(1L, "john@example.com");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCaseService.login("unknown@example.com", "pwd"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncryption.matches("wrong", "encrypted")).thenReturn(false);

        assertThatThrownBy(() -> userUseCaseService.login("john@example.com", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldRefreshAndReturnNewTokens() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        RefreshToken storedToken = new RefreshToken(10L, "old-refresh", 1L,
                FIXED_TIME.plusYears(100), false, FIXED_TIME);

        when(tokenService.validateRefreshTokenAndGetUserId("old-refresh")).thenReturn(1L);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(storedToken));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(1L, "john@example.com")).thenReturn("new-access");
        when(tokenService.generateRefreshToken(1L, "john@example.com")).thenReturn("new-refresh");

        Authentication result = userUseCaseService.refresh("old-refresh");

        assertThat(result.getAccessToken()).isEqualTo("new-access");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(result.getUser().getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenRefreshTokenRevoked() {
        RefreshToken revokedToken = new RefreshToken(10L, "old-refresh", 1L,
                FIXED_TIME.plusYears(100), true, FIXED_TIME);

        when(tokenService.validateRefreshTokenAndGetUserId("old-refresh")).thenReturn(1L);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> userUseCaseService.refresh("old-refresh"))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token has been revoked. All sessions invalidated.");

        verify(refreshTokenRepository).revokeAllForUser(1L);
    }

    @Test
    void shouldThrowWhenRefreshTokenExpired() {
        RefreshToken expiredToken = new RefreshToken(10L, "old-refresh", 1L,
                FIXED_TIME.minusDays(1), false, FIXED_TIME);

        when(tokenService.validateRefreshTokenAndGetUserId("old-refresh")).thenReturn(1L);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> userUseCaseService.refresh("old-refresh"))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token has expired");
    }

    @Test
    void shouldLogoutAndRevokeToken() {
        RefreshToken storedToken = new RefreshToken(10L, "refresh-value", 1L,
                FIXED_TIME.plusDays(7), false, FIXED_TIME);

        when(refreshTokenRepository.findByToken("refresh-value")).thenReturn(Optional.of(storedToken));

        userUseCaseService.logout("refresh-value");

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldRejectPurelyAlphabeticPassword() {
        User input = new User(null, "john", "john@example.com", "adminadmin", null);
        UserRegistrationService realService = new UserRegistrationService(userRepository, 8, true);
        var service = new UserUseCaseService(userRepository, passwordEncryption,
                realService, tokenService, refreshTokenRepository, REFRESH_EXPIRATION_MS);

        assertThatThrownBy(() -> service.register(input))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessage("Password must not be purely alphabetic");
    }

    @Test
    void shouldPropagateWeakPasswordException() {
        User input = new User(null, "john", "john@example.com", "12345678", null);
        WeakPasswordException exception = new WeakPasswordException("Password must not be purely numeric");
        doThrow(exception).when(userRegistrationService).validate(input);

        assertThatThrownBy(() -> userUseCaseService.register(input))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessage("Password must not be purely numeric");
    }

}

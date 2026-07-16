package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.UserUseCase;
import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;
import com.iignaasii47.e_commerce_api.domain.exception.RefreshTokenException;
import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.RefreshToken;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.PasswordEncryption;
import com.iignaasii47.e_commerce_api.domain.port.out.RefreshTokenRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;
import com.iignaasii47.e_commerce_api.domain.service.UserRegistrationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class UserUseCaseService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryption passwordEncryption;
    private final UserRegistrationService userRegistrationService;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationSeconds;

    public UserUseCaseService(UserRepository userRepository,
                               PasswordEncryption passwordEncryption,
                               UserRegistrationService userRegistrationService,
                               TokenService tokenService,
                               RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh-token-expiration-ms}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncryption = passwordEncryption;
        this.userRegistrationService = userRegistrationService;
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationSeconds = refreshExpirationMs / 1000;
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
    @Transactional
    public Authentication login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncryption.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
        String refreshTokenValue = tokenService.generateRefreshToken(user.getId(), user.getEmail());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        RefreshToken refreshToken = new RefreshToken(null, refreshTokenValue, user.getId(),
                now.plusSeconds(refreshExpirationSeconds), false, now);
        refreshTokenRepository.save(refreshToken);

        return new Authentication(user, accessToken, refreshTokenValue);
    }

    @Override
    @Transactional
    public Authentication refresh(String refreshTokenValue) {
        Long userId = tokenService.validateRefreshTokenAndGetUserId(refreshTokenValue);

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        if (storedToken.isRevoked()) {
            refreshTokenRepository.revokeAllForUser(userId);
            throw new RefreshTokenException("Refresh token has been revoked. All sessions invalidated.");
        }

        if (storedToken.isExpired()) {
            throw new RefreshTokenException("Refresh token has expired");
        }

        refreshTokenRepository.deleteExpired();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RefreshTokenException("User not found"));

        var revokedToken = new RefreshToken(storedToken.getId(), storedToken.getToken(),
                storedToken.getUserId(), storedToken.getExpiryDate(), true, storedToken.getCreatedAt());
        refreshTokenRepository.save(revokedToken);

        String newAccessToken = tokenService.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshTokenValue = tokenService.generateRefreshToken(user.getId(), user.getEmail());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        RefreshToken newRefreshToken = new RefreshToken(null, newRefreshTokenValue, user.getId(),
                now.plusSeconds(refreshExpirationSeconds), false, now);
        refreshTokenRepository.save(newRefreshToken);

        return new Authentication(user, newAccessToken, newRefreshTokenValue);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        if (!storedToken.isRevoked()) {
            var revokedToken = new RefreshToken(storedToken.getId(), storedToken.getToken(),
                    storedToken.getUserId(), storedToken.getExpiryDate(), true, storedToken.getCreatedAt());
            refreshTokenRepository.save(revokedToken);
        }
    }

}

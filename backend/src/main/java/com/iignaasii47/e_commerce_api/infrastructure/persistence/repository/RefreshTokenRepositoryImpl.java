package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.RefreshToken;
import com.iignaasii47.e_commerce_api.domain.port.out.RefreshTokenRepository;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper.RefreshTokenMapper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRepository;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(RefreshToken token) {
        var entity = RefreshTokenMapper.toEntity(token);
        jpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(RefreshTokenMapper::toDomain);
    }

    @Override
    @Transactional
    public void revokeAllForUser(Long userId) {
        jpaRepository.revokeAllForUser(userId);
    }

    @Override
    @Transactional
    public void deleteExpired() {
        jpaRepository.deleteExpired(LocalDateTime.now(ZoneOffset.UTC));
    }

}

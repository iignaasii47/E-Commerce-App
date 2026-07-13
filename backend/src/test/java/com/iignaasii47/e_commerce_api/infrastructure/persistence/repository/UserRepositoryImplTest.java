package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserRepositoryImpl userRepositoryImpl;

    @Test
    void shouldSaveUser() {
        User user = new User(null, "john", "john@example.com", "encrypted", null);
        UserEntity savedEntity = new UserEntity(1L, "john", "john@example.com", "encrypted", FIXED_TIME);

        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        User result = userRepositoryImpl.save(user);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getCreatedAt()).isEqualTo(FIXED_TIME);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(jpaUserRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("john");
    }

    @Test
    void shouldFindByUsername() {
        UserEntity entity = new UserEntity(1L, "john", "john@example.com", "pwd", FIXED_TIME);
        when(jpaUserRepository.findByUsername("john")).thenReturn(Optional.of(entity));

        Optional<User> result = userRepositoryImpl.findByUsername("john");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john");
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldFindByEmail() {
        UserEntity entity = new UserEntity(1L, "john", "john@example.com", "pwd", FIXED_TIME);
        when(jpaUserRepository.findByEmail("john@example.com")).thenReturn(Optional.of(entity));

        Optional<User> result = userRepositoryImpl.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByUsername() {
        when(jpaUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userRepositoryImpl.findByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckExistsByUsername() {
        when(jpaUserRepository.existsByUsername("john")).thenReturn(true);
        when(jpaUserRepository.existsByUsername("unknown")).thenReturn(false);

        assertThat(userRepositoryImpl.existsByUsername("john")).isTrue();
        assertThat(userRepositoryImpl.existsByUsername("unknown")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        when(jpaUserRepository.existsByEmail("john@example.com")).thenReturn(true);
        when(jpaUserRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        assertThat(userRepositoryImpl.existsByEmail("john@example.com")).isTrue();
        assertThat(userRepositoryImpl.existsByEmail("unknown@example.com")).isFalse();
    }

}

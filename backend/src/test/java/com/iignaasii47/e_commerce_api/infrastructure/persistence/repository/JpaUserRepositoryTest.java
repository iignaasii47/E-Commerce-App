package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void shouldSaveAndFindById() {
        UserEntity entity = new UserEntity(null, "john", "john@example.com", "encrypted", null);
        UserEntity saved = jpaUserRepository.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<UserEntity> found = jpaUserRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john");
    }

    @Test
    void shouldFindByUsername() {
        jpaUserRepository.save(new UserEntity(null, "john", "john@example.com", "encrypted", null));

        Optional<UserEntity> result = jpaUserRepository.findByUsername("john");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnEmptyForUnknownUsername() {
        Optional<UserEntity> result = jpaUserRepository.findByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindByEmail() {
        jpaUserRepository.save(new UserEntity(null, "john", "john@example.com", "encrypted", null));

        Optional<UserEntity> result = jpaUserRepository.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john");
    }

    @Test
    void shouldCheckExistsByUsername() {
        jpaUserRepository.save(new UserEntity(null, "john", "john@example.com", "encrypted", null));

        assertThat(jpaUserRepository.existsByUsername("john")).isTrue();
        assertThat(jpaUserRepository.existsByUsername("unknown")).isFalse();
    }

    @Test
    void shouldCheckExistsByEmail() {
        jpaUserRepository.save(new UserEntity(null, "john", "john@example.com", "encrypted", null));

        assertThat(jpaUserRepository.existsByEmail("john@example.com")).isTrue();
        assertThat(jpaUserRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    void shouldEnforceUniqueUsername() {
        jpaUserRepository.save(new UserEntity(null, "john", "john@example.com", "encrypted", null));
        UserEntity duplicate = new UserEntity(null, "john", "other@example.com", "encrypted", null);

        assertThatThrownBy(() -> jpaUserRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldEnforceUniqueEmail() {
        jpaUserRepository.save(new UserEntity(null, "john", "john@example.com", "encrypted", null));
        UserEntity duplicate = new UserEntity(null, "other", "john@example.com", "encrypted", null);

        assertThatThrownBy(() -> jpaUserRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}

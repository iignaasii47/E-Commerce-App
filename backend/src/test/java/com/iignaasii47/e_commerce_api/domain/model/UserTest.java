package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldConstructWithAllFields() {
        User user = new User(1L, "john", "john@example.com", "password", FIXED_TIME);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldBeEqualWhenSameId() {
        User user1 = new User(1L, "john", "john@example.com", "pass", FIXED_TIME);
        User user2 = new User(1L, "jane", "jane@example.com", "word", FIXED_TIME.plusDays(1));

        assertThat(user1).isEqualTo(user2).hasSameHashCodeAs(user2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        User user1 = new User(1L, "john", "john@example.com", "pass", FIXED_TIME);
        User user2 = new User(2L, "john", "john@example.com", "pass", FIXED_TIME);

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        User user = new User(1L, "john", "john@example.com", "pass", FIXED_TIME);

        assertThat(user).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        User user = new User(1L, "john", "john@example.com", "pass", FIXED_TIME);

        assertThat(user).isNotEqualTo("not a user");
    }

    @Test
    void shouldHashToSameValueWhenSameId() {
        User user1 = new User(1L, "john", "john@example.com", "pass", FIXED_TIME);
        User user2 = new User(1L, "jane", "jane@example.com", "word", FIXED_TIME.plusDays(1));

        assertThat(user1).hasSameHashCodeAs(user2);
    }

}

package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void shouldMapUserToResponse() {
        User user = TestFixtures.aJohnUser();

        UserResponse response = UserResponse.from(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(TestFixtures.FIXED_TIME);
    }

    @Test
    void shouldContainAllFieldsWithConstructor() {
        UserResponse response = new UserResponse(1L, "john", "john@example.com", TestFixtures.FIXED_TIME);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getCreatedAt()).isNotNull();
    }

}

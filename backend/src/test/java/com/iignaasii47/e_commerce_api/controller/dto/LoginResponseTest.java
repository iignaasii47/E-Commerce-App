package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    @Test
    void shouldMapAuthenticationToResponse() {
        User user = TestFixtures.aJohnUser();
        Authentication auth = new Authentication(user, "jwt-token");

        LoginResponse response = LoginResponse.from(auth);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(TestFixtures.FIXED_TIME);
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void shouldSetAndGetToken() {
        LoginResponse response = new LoginResponse();
        response.setToken("jwt-token");

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

}

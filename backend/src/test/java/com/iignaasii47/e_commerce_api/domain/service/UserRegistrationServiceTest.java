package com.iignaasii47.e_commerce_api.domain.service;

import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.exception.WeakPasswordException;
import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.domain.port.out.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    private static final int MIN_LENGTH = 8;
    private static final boolean REQUIRE_MIXED = true;

    @Mock
    private UserRepository userRepository;

    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setup() {
        userRegistrationService = new UserRegistrationService(userRepository, MIN_LENGTH, REQUIRE_MIXED);
    }

    @Test
    void shouldPassWhenUsernameAndEmailAreUnique() {
        User user = new User(null, "john", "john@example.com", "Pass1234!", null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        assertThatCode(() -> userRegistrationService.validate(user)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenUsernameExists() {
        User user = new User(null, "john", "john@example.com", "Pass1234!", null);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Username 'john' is already taken");
    }

    @Test
    void shouldThrowWhenEmailExists() {
        User user = new User(null, "john", "john@example.com", "Pass1234!", null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Email 'john@example.com' is already registered");
    }

    @Test
    void shouldThrowWhenPasswordIsTooShort() {
        User user = new User(null, "john", "john@example.com", "Aa1!", null);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessage("Password must be at least 8 characters long");
    }

    @Test
    void shouldThrowWhenPasswordIsPurelyNumeric() {
        User user = new User(null, "john", "john@example.com", "12345678", null);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessage("Password must not be purely numeric");
    }

    @Test
    void shouldThrowWhenPasswordIsPurelyAlphabetic() {
        User user = new User(null, "john", "john@example.com", "abcdefgh", null);

        assertThatThrownBy(() -> userRegistrationService.validate(user))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessage("Password must not be purely alphabetic");
    }

    @Test
    void shouldPassWhenPasswordIsValidMixedCharacters() {
        User user = new User(null, "alice", "alice@example.com", "Str0ngP@ss!", null);
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);

        assertThatCode(() -> userRegistrationService.validate(user)).doesNotThrowAnyException();
    }

    @Test
    void shouldPassNumericPasswordWhenMixedDisabled() {
        UserRegistrationService service = new UserRegistrationService(userRepository, 8, false);
        User user = new User(null, "john", "john@example.com", "12345678", null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        assertThatCode(() -> service.validate(user)).doesNotThrowAnyException();
    }

}

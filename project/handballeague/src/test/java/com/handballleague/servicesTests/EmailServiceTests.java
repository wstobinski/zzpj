package com.handballleague.servicesTests;

import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.User;
import com.handballleague.repositories.UserRepository;
import com.handballleague.services.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {
    @Mock
    private UserRepository userRepository;

    private AutoCloseable autoCloseable;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        emailService = new EmailService(userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void sendEmail_WithInvalidEmail_ThrowsException() {
        // Given
        String email = null;

        // When & Then
        assertThatThrownBy(() -> emailService.sendEmail(email, ""))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed email is invalid.");
    }

    @Test
    void sendEmail_UserNotFound_ThrowsException() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> emailService.sendEmail(email, ""))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("User with given email was not found in database.");
    }

    @Test
    void activateAcc_WithValidCodeAndPass_ReturnsUser() {
        // Given
        int code = 123456;
        String password = "securePassword";
        User user = new User("john.doe@example.com", password, "user");
        user.setCode(code);
        when(userRepository.findByCode(code)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // When
        User activatedUser = emailService.activateAcc(code, password);

        // Then
        assertThat(activatedUser).isNotNull();
        assertThat(activatedUser.isActive()).isTrue();
        assertThat(activatedUser.getCode()).isEqualTo(0);
    }

    @Test
    void activateAcc_WithInvalidPassword_ThrowsException() {
        // Given
        int code = 123456;
        String password = null;

        // When & Then
        assertThatThrownBy(() -> emailService.activateAcc(code, password))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed password is invalid.");
    }

    @Test
    void activateAcc_WithInvalidCode_ThrowsException() {
        // Given
        int invalidCode = 999999;
        String password = "securePassword";
        when(userRepository.findByCode(invalidCode)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> emailService.activateAcc(invalidCode, password))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("User with given code was not found in database.");
    }
}
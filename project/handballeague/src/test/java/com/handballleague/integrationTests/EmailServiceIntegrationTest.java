package com.handballleague.integrationTests;

import com.handballleague.exceptions.InvalidArgumentException;
import  com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import  com.handballleague.model.User;
import  com.handballleague.repositories.UserRepository;
import  com.handballleague.services.EmailService;
import  org.junit.jupiter.api.Test;
import  org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.boot.test.context.SpringBootTest;
import  org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import  org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmailServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Test
    void activateAcc_WithValidCodeAndPass_ReturnsUser() {
        // Given
        int code = 123456;
        String password = "securePassword";
        User user = new User("john.doe@example.com", password, "user");
        user.setCode(code);
        userRepository.save(user);

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
        User user = new User("john.doe@example.com", "somePassword", "user");
        user.setCode(code);
        userRepository.save(user);

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

        // When & Then
        assertThatThrownBy(() -> emailService.activateAcc(invalidCode, password))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("User with given code was not found in database.");
    }
}

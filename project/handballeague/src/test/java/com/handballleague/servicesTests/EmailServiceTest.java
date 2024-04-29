package com.handballleague.servicesTests;

import com.handballleague.repositories.UserRepository;
import com.handballleague.services.EmailService;
import com.handballleague.services.JWTService;
import com.handballleague.services.UserService;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
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
    void sendEmail() {
    }

    @Test
    void activateAcc() {
    }
}
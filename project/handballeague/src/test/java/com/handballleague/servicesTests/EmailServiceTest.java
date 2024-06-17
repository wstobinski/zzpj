package com.handballleague.servicesTests;

import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.User;
import com.handballleague.repositories.UserRepository;
import com.handballleague.services.EmailService;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;
import java.util.Properties;

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

    @Test
    void sendEmail_WithValidEmailAndRole_ReturnsMessage() throws MessagingException {
        // Given
        String email = "john.doe@example.com";
        String role = "captain";
        User user = new User(email, "password123", role);
        user.setCode(123456);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Mock Session for sending email
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.example.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("username", "password");
            }
        });

//        Transport transport = session.getTransport("smtp");
//        transport.connect("smtp.example.com", "username", "password");

        when(userRepository.save(user)).thenReturn(user);
        Message message = new MimeMessage(session);

        try (MockedStatic<Transport> transport = Mockito.mockStatic(Transport.class)) {
            transport.when(() -> Transport.send(message)).thenAnswer(invocation -> null);

            //test
            Message sentMessage = emailService.sendEmail(email, role);

            assertThat(sentMessage).isNotNull();
            assertThat(sentMessage.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo(email);
            assertThat(sentMessage.getSubject()).isEqualTo("Witamy w HandBallLeague!");
            verify(userRepository).save(user);
        }

        // When
//        Message sentMessage = emailService.sendEmail(email, role);
//
//        // Then
//        assertThat(sentMessage).isNotNull();
//        assertThat(sentMessage.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo(email);
//        assertThat(sentMessage.getSubject()).isEqualTo("Witamy w HandBallLeague!");
//        verify(userRepository).save(user);
    }
}
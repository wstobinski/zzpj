package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.User;
import com.handballleague.repositories.UserRepository;
import com.handballleague.services.JWTService;
import com.handballleague.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JWTService jwtService;

    private AutoCloseable autoCloseable;
    private UserService userService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, jwtService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createUser_WithValidInput_ReturnsUser() {
        // Given
        User user = new User("john.doe@example.com", "password123", "user");

        // When
        userService.create(user);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void createUser_ThatAlreadyExists_ThrowsException() {
        // Given
        User existingUser = new User("john.doe@example.com", "password123", "user");
        when(userRepository.existsByEmail(existingUser.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(existingUser))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("User with given data already exists in database");

        verify(userRepository, never()).save(any()); // No save operation should be performed
    }

    @Test
    void createUser_WithNullEntity_ThrowsException() {
        // When & Then
        assertThatThrownBy(() -> userService.create(null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");
    }

    @Test
    void createUser_WithNullOrEmptyFields_ThrowsException() {
        // Given
        User userWithNullOrEmptyFields = new User("", "", "");

        // When & Then
        assertThatThrownBy(() -> userService.create(userWithNullOrEmptyFields))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of user parameters is invalid.");
    }

    @Test
    void createUser_PasswordHashing() {
        // Given
        String password = "password123";
        User user = new User("john.doe@example.com", password, "user");

        // When
        userService.create(user);

        // Then
        assertThat(user.getPassword()).isNotBlank();
        assertThat(user.getPassword()).isNotEqualTo(password); // Password should be hashed
        assertThat(BCrypt.checkpw(password, user.getPassword())).isTrue(); // Checking if password matches hashed password
    }

    @Test
    void logInUser_ExistingUser_CorrectPassword_ReturnsToken() {
        // Given
        User existingUser = new User("john.doe@example.com", "password123", "user");
        User existingUserWithHash = new User("john.doe@example.com", "password123", "user");
        String hashedPassword = BCrypt.hashpw(existingUser.getPassword(), BCrypt.gensalt());
        existingUserWithHash.setPassword(hashedPassword);

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(existingUserWithHash);
        when(jwtService.generateToken(existingUser)).thenReturn("generatedToken");

        // When
        String token = userService.logInUser(existingUser);

        // Then
        assertThat(token).isNotNull().isEqualTo("generatedToken");
        verify(jwtService).generateToken(existingUser);
    }

    @Test
    void logInUser_NonExistingUser_ThrowsException() {
        // Given
        String email = "nonexisting@example.com";
        User nonExistingUser = new User(email, "password", "user");
        when(userRepository.findByEmail(email)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> userService.logInUser(nonExistingUser))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("User does not exist.");
    }

    @Test
    void logInUser_ExistingUser_WrongPassword_ThrowsException() {
        // Given
        String email = "john.doe@example.com";
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt());
        User existingUser = new User(email, hashedPassword, "user");
        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        // When & Then
        assertThatThrownBy(() -> userService.logInUser(new User(email, wrongPassword, "user")))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Wrong password!");
    }

    @Test
    void updateUser_WithValidIdAndEntity_ReturnsUpdatedUser() {
        // Given
        Long id = 1L;
        String email = "john.doe@example.com";
        String password = "password123";
        String role = "user";
        User existingUser = new User(email, password, role);
        existingUser.setUuid(id);

        String newEmail = "john.smith@example.com";
        String newPassword = "newpassword456";
        String newRole = "admin";
        User updatedUser = new User(newEmail, newPassword, newRole);
        updatedUser.setUuid(id);

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // When
        User result = userService.update(id, updatedUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(id);
        assertThat(result.getEmail()).isEqualTo(newEmail);
        assertThat(result.getPassword()).isNotEqualTo(password); // Password should be updated and hashed
        assertThat(result.getRole()).isEqualTo(newRole);
    }

    @Test
    void updateUser_WithInvalidId_ThrowsException() {
        // Given
        Long invalidId = 0L;
        User entity = new User("john.doe@example.com", "password123", "user");

        // When & Then
        assertThatThrownBy(() -> userService.update(invalidId, entity))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");
    }

    @Test
    void updateUser_WithNullEntity_ThrowsException() {
        // Given
        Long id = 1L;

        // When & Then
        assertThatThrownBy(() -> userService.update(id, null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New user is null.");
    }

    @Test
    void updateUser_WithNullOrEmptyFields_ThrowsException() {
        // Given
        Long id = 1L;
        User entity = new User("", "", "");

        // When & Then
        assertThatThrownBy(() -> userService.update(id, entity))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of user parameters is invalid.");
    }

    @Test
    void deleteUser_WithValidId_DeletesUserAndReturnsTrue() {
        // Given
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = userService.delete(id);

        // Then
        assertThat(result).isTrue();
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteUser_WithInvalidId_ThrowsException() {
        // Given
        Long invalidId = 0L;

        // When & Then
        assertThatThrownBy(() -> userService.delete(invalidId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");
        verify(userRepository, never()).deleteById(invalidId);
    }

    @Test
    void deleteUser_NonExistingUser_ThrowsException() {
        // Given
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("User with id: " + id + " not found in database.");
        verify(userRepository, never()).deleteById(id);
    }
}

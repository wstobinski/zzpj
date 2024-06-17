package com.handballleague.servicesTests;

import com.handballleague.model.Player;
import com.handballleague.model.User;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.UserRepository;
import com.handballleague.services.JWTService;
import com.handballleague.services.PlayerService;
import com.handballleague.services.RefereeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JWTServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private RefereeService refereeService;

    private JWTService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JWTService(userRepository, playerRepository, playerService, refereeService);
        jwtService.setSecretKey(this.secretKey);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        User user = new User("test@example.com", "password", "role");
        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull();

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
        assertThat(claims.get("role")).isEqualTo(user.getRole());
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        User user = new User("test@example.com", "password", "role");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    void isTokenExpired_ShouldReturnTrueForExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "role");

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                .setExpiration(new Date(System.currentTimeMillis() - 1800000))
                .signWith(SignatureAlgorithm.HS384, secretKey)
                .compact();

        assertThat(jwtService.isTokenExpired(token)).isTrue();
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        User user = new User("test@example.com", "password", "role");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractRole(token)).isEqualTo(user.getRole());
    }

    @Test
    void extractSubject_ShouldReturnCorrectSubject() {
        User user = new User("test@example.com", "password", "role");
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractSubject(token)).isEqualTo(user.getEmail());
    }

    @Test
    void handleAuthorization_ShouldReturnUnauthorizedForMissingToken() {
        ResponseEntity<?> response = jwtService.handleAuthorization(null, "role");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(Map.of("ok", false, "message", "JWT token missing"));
    }

    @Test
    void handleAuthorization_ShouldReturnUnauthorizedForExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "role");

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                .setExpiration(new Date(System.currentTimeMillis() - 1800000))
                .signWith(SignatureAlgorithm.HS384, secretKey)
                .compact();

        ResponseEntity<?> response = jwtService.handleAuthorization(token, "role");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(Map.of("ok", false, "message", "JWT token expired"));
    }

    @Test
    void handleAuthorization_ShouldReturnForbiddenForIncorrectRole() {
        User user = new User("test@example.com", "password", "wrongRole");
        String token = jwtService.generateToken(user);

        ResponseEntity<?> response = jwtService.handleAuthorization(token, "expectedRole");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(Map.of("ok", false, "message", "Insufficient role"));
    }

    @Test
    void handleAuthorization_ShouldReturnOkForValidTokenAndRole() {
        User user = new User("test@example.com", "password", "expectedRole");
        String token = jwtService.generateToken(user);

        ResponseEntity<?> response = jwtService.handleAuthorization(token, "expectedRole");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Map.of("ok", true, "message", "Token and role are valid"));
    }

    @Test
    void tokenToModel_ShouldReturnPlayerForCaptainRole() {
        User user = new User("captain@example.com", "password", "captain");
        user.setModelId(1L);

        String token = jwtService.generateToken(user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        Player player = new Player();
        when(playerService.getById(user.getModelId())).thenReturn(player);

        Object result = jwtService.tokenToModel(token);

        assertThat(result).isInstanceOf(Player.class);
        assertThat(result).isEqualTo(player);
    }

    @Test
    void tokenToModel_ShouldThrowExceptionForInvalidToken() {
        User user = new User();
        String token = jwtService.generateToken(user);

        assertThatThrownBy(() -> jwtService.tokenToModel(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void tokenToModel_ShouldReturnNullForUnknownRole() {
        User user = new User("unknown@example.com", "password", "unknown");
        user.setModelId(1L);

        String token = jwtService.generateToken(user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));

        Object result = jwtService.tokenToModel(token);

        assertThat(result).isNull();
    }
}

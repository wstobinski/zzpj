package com.handballleague.services;

import com.handballleague.model.Player;
import com.handballleague.model.User;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private final RefereeService refereeService;
    @Value("${jwt.secret}")
    private String secretKey;
    private static final long EXPIRATION_TIME = 3600000;

    public JWTService(UserRepository userRepository, PlayerRepository playerRepository, PlayerService playerService, RefereeService refereeService) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.refereeService = refereeService;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS384, secretKey)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("role");
    }

    public String extractSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public ResponseEntity<?> handleAuthorization(String token, String roleToCheck) {
        if  (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "JWT token missing"));
        if (isTokenExpired(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "JWT token expired"));
        if (!roleToCheck.equals(extractRole(token))) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("ok", false, "message", "Insufficient role"));
        return ResponseEntity.ok(Map.of("ok", true, "message", "Token and role are valid"));
    }

    public Object tokenToModel(String token) {

        String email = extractSubject(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole().equals("captain")) {

            return playerService.getById(user.getModelId());

        } else if (user.getRole().equals("arbiter")){
            return refereeService.getById(user.getModelId());
        }

        return null;
    }

}

package com.handballleague.services;

import com.handballleague.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
public class JWTService {
    @Value("${jwt.secret}")
    private String secretKey;
    private static final long EXPIRATION_TIME = 3600000;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setClaims(claims)
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

    public ResponseEntity<?> handleAuthorization(String token, String roleToCheck) {
        if (token != null) {
            if (!isTokenExpired(token)) {
                String role = extractRole(token);
                if (!roleToCheck.equals(role)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient role");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT token");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token missing");
        }
        return ResponseEntity.ok("Token and role are valid");
    }

}

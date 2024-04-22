package com.handballleague.controllers;

import com.handballleague.model.User;
import com.handballleague.services.JWTService;
import com.handballleague.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {
    private final UserService userService;
    private final JWTService jwtService;

    @Autowired
    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestHeader(name = "Authorization") String token, @RequestBody User entity) {
        try {
            ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
            if (response.getStatusCode().is2xxSuccessful()) {
                userService.create(entity);
                return ResponseEntity.ok("User created successfully");
            } else {
                return response;
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> logInUser(@RequestBody User entity) {
        try {
            String token = userService.logInUser(entity);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers(@RequestHeader(name = "Authorization") String token) {
        try {
            ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
            if (response.getStatusCode().is2xxSuccessful()) {
                List<User> users = userService.getAll();
                return ResponseEntity.ok(users);
            } else {
                return response;
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

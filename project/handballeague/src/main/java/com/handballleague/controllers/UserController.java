package com.handballleague.controllers;

import com.handballleague.model.User;
import com.handballleague.services.EmailService;
import com.handballleague.services.JWTService;
import com.handballleague.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {
    private final UserService userService;
    private final JWTService jwtService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, JWTService jwtService, EmailService emailService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestHeader(name = "Authorization") String token, @RequestBody User entity) {

        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            userService.create(entity);
            emailService.sendEmail(entity.getEmail());
            return ResponseEntity.ok("User created successfully");
        } else {
            return response;
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateUser(Map<String, Object> requestBody) {
        int code = (int) requestBody.get("code");
        String password = (String) requestBody.get("password");
        emailService.activateAcc(code, password);
        return ResponseEntity.ok("User account successfully activated!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> logInUser(@RequestBody User entity) {

        String token = userService.logInUser(entity);
        return ResponseEntity.ok().body(Map.of("token", token));

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            User newUser = userService.update(userId, user);
            return ResponseEntity.ok(newUser);
        } else {
            ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
            if (response2.getStatusCode().is2xxSuccessful()) {
                User newUser = userService.update(userId, user);
                return ResponseEntity.ok(newUser);
            } else {
                ResponseEntity<?> response3 = jwtService.handleAuthorization(token, "arbiter");
                if (response3.getStatusCode().is2xxSuccessful()) {
                    User newUser = userService.update(userId, user);
                    return ResponseEntity.ok(newUser);
                } else {
                    return response3;
                }
            }
        }
    }

    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers(@RequestHeader(name = "Authorization") String token) {

        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            List<User> users = userService.getAll();
            return ResponseEntity.ok(users);
        } else {
            return response;
        }

    }

}

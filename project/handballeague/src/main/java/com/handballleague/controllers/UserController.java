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
            return ResponseEntity.ok(Map.of("ok", true, "message", "User created successfully"));
        } else {
            return response;
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody String token) {
        String test = jwtService.extractSubject(token);
        return ResponseEntity.ok(test);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader(name = "Authorization") String token, @RequestBody Map<String, String> credentials) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        String email = jwtService.extractSubject(token);
        if (response.getStatusCode().is2xxSuccessful()) {
            User newUser = userService.changePassword(email, credentials.get("oldPassword"), credentials.get("newPassword"));
            return ResponseEntity.ok(Map.of("ok", true, "response", newUser));
        }
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response2.getStatusCode().is2xxSuccessful()) {
            User newUser = userService.changePassword(email, credentials.get("oldPassword"), credentials.get("newPassword"));
            return ResponseEntity.ok(Map.of("ok", true, "response", newUser));
        }
        ResponseEntity<?> response3 = jwtService.handleAuthorization(token, "arbiter");
        if (response3.getStatusCode().is2xxSuccessful()) {
            User newUser = userService.changePassword(email, credentials.get("oldPassword"), credentials.get("newPassword"));
            return ResponseEntity.ok(Map.of("ok", true, "response", newUser));
        } else {
            return response3;
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

        User userToLogin = userService.getByEmail(entity.getEmail());
        String token = userService.logInUser(entity);
        return ResponseEntity.ok().body(Map.of("response", Map.of("token", token, "user", userToLogin),
                "ok", true));

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            User newUser = userService.update(userId, user);
            return ResponseEntity.ok(Map.of("ok", true, "response", newUser));
        } else {
            ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
            if (response2.getStatusCode().is2xxSuccessful()) {
                User newUser = userService.update(userId, user);
                return ResponseEntity.ok(Map.of("ok", true, "response", newUser));
            } else {
                ResponseEntity<?> response3 = jwtService.handleAuthorization(token, "arbiter");
                if (response3.getStatusCode().is2xxSuccessful()) {
                    User newUser = userService.update(userId, user);
                    return ResponseEntity.ok(Map.of("ok", true, "response", newUser));
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

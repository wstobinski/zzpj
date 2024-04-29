package com.handballleague.controllers;

import com.handballleague.model.User;
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

    @Autowired
    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestHeader(name = "Authorization") String token, @RequestBody User entity) {

        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            userService.create(entity);
            return ResponseEntity.ok("User created successfully");
        } else {
            return response;
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> logInUser(@RequestBody User entity) {

        String token = userService.logInUser(entity);
        return ResponseEntity.ok().body(Map.of("response", Map.of("token", token, "user", entity),
                "ok", true));

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            User newUser = userService.update(userId, user);
            return ResponseEntity.ok(newUser);
        } else {
            ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
            if (response2.getStatusCode().is2xxSuccessful()){
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

package com.handballleague.controllers;

import com.handballleague.model.Player;
import com.handballleague.model.TeamContest;
import com.handballleague.services.JWTService;
import com.handballleague.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/players")
public class PlayerController {
    private final PlayerService playerService;
    private final JWTService jwtService;
    @Autowired
    public PlayerController(PlayerService playerService, JWTService jwtService) {
        this.playerService = playerService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {

        List<Player> players = playerService.getAll();
        return ResponseEntity.ok(players);

    }

    @PostMapping()
    public ResponseEntity<?> registerNewPlayer(@Valid @RequestBody Player player, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            playerService.create(player);
            return ResponseEntity.ok("Player created successfully");
        } else {
            return response2;
        }
    }

    @DeleteMapping(path = "/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable("playerId") Long id, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response1 = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response1.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            boolean deleted = playerService.delete(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("Deleted state", deleted);
            return ResponseEntity.ok(response);
        } else {
            return response2;
        }
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayerById(@PathVariable Long playerId) {

        Player player = playerService.getById(playerId);
        return ResponseEntity.ok(player);

    }

    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@Valid @PathVariable Long playerId, @RequestBody Player player, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Player newPlayer = playerService.update(playerId, player);
            return ResponseEntity.ok(newPlayer);
        } else {
            return response2;
        }
    }

}

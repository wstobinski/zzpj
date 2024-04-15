package com.handballleague.controllers;

import com.handballleague.model.Player;
import com.handballleague.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/players")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {
        try {
            List<Player> players = playerService.getAll();
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<String> registerNewPlayer(@RequestBody Player player) {
        try {
            playerService.create(player);
            return ResponseEntity.ok("Player created successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to create player: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping(path = "/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable("playerId") Long id) {
        try {
            boolean deleted = playerService.delete(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("Deleted state", deleted);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayerById(@PathVariable Long playerId) {
        try {
            Player player = playerService.getById(playerId);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@PathVariable Long playerId, @RequestBody Player player) {
        try {
            Player newPlayer = playerService.update(playerId, player);
            return ResponseEntity.ok(newPlayer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

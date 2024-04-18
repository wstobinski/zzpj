package com.handballleague.controllers;

import com.handballleague.model.Player;
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

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {

        List<Player> players = playerService.getAll();
        return ResponseEntity.ok(players);

    }

    @PostMapping()
    public ResponseEntity<String> registerNewPlayer(@Valid @RequestBody Player player) {
        playerService.create(player);
        return ResponseEntity.ok("Player created successfully");
    }

    @DeleteMapping(path = "/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable("playerId") Long id) {
        boolean deleted = playerService.delete(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted state", deleted);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayerById(@PathVariable Long playerId) {

        Player player = playerService.getById(playerId);
        return ResponseEntity.ok(player);

    }

    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@Valid @PathVariable Long playerId, @RequestBody Player player) {

        Player newPlayer = playerService.update(playerId, player);
        return ResponseEntity.ok(newPlayer);

    }

}

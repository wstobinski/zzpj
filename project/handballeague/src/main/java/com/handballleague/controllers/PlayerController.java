package com.handballleague.controllers;

import com.handballleague.model.POSITIONS;
import com.handballleague.model.Player;
import com.handballleague.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedList;
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
    public List<Player> getPlayers() {
        return playerService.getAll();
    }

    @PostMapping
    public void registerNewPlayer(@RequestBody Player player) {
        playerService.create(player);
    }

    @DeleteMapping(path = "{playerId}")
    public ResponseEntity<Map<String, Boolean>> deletePlayer(@PathVariable("playerId") Long id) {
        boolean deleted = playerService.delete(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted state", deleted);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long playerId) {
        Player player = playerService.getById(playerId);
        return ResponseEntity.ok(player);
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long playerId, @RequestBody Player player) {
        Player newPlayer = playerService.update(playerId, player);

        return ResponseEntity.ok(newPlayer);
    }

}

package com.handballleague.controllers;

import com.handballleague.DTO.GeneratePlayersDTO;
import com.handballleague.initialization.PlayersInitializer;
import com.handballleague.model.Player;
import com.handballleague.services.JWTService;
import com.handballleague.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/players")
public class PlayerController {
    private final PlayerService playerService;
    private final JWTService jwtService;
    private final PlayersInitializer playersInitializer;

    @Autowired
    public PlayerController(PlayerService playerService, JWTService jwtService, PlayersInitializer playersInitializer) {
        this.playerService = playerService;
        this.jwtService = jwtService;
        this.playersInitializer = playersInitializer;
    }

    @GetMapping
    public ResponseEntity<?> getPlayers() {

        List<Player> players = playerService.getAll();
        return ResponseEntity.ok().body(Map.of("response", players,
                "ok", true));

    }

    @GetMapping("/free-agents")
    public ResponseEntity<?> getFreeAgents() {
        List<Player> players = playerService.getFreeAgents();
        return ResponseEntity.ok().body(Map.of("response", players,
                "ok", true));

    }

    @PostMapping()
    public ResponseEntity<?> registerNewPlayer(@Valid @RequestBody Player player, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Player newPlayer = playerService.create(player);
            return ResponseEntity.ok().body(Map.of("message", "Player created successfully",
                    "response", newPlayer,
                    "ok", true));
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
            return ResponseEntity.ok(Map.of("ok", deleted));
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
        ResponseEntity<?> response3 = jwtService.handleAuthorization(token, "arbiter");
        if (response.getStatusCode().is2xxSuccessful() ||
                response2.getStatusCode().is2xxSuccessful() ||
                response3.getStatusCode().is2xxSuccessful()) {
            Player newPlayer = playerService.update(playerId, player);
            return ResponseEntity.ok(Map.of("ok", true, "response", newPlayer));
        } else {
            return response3;
        }
    }

    @PostMapping("/generate-players")
    public ResponseEntity<?> generatePlayers(@RequestBody GeneratePlayersDTO body, @RequestHeader(name = "Authorization") String token) {
            ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
            if (!response.getStatusCode().is2xxSuccessful()) {
                return response;
            }
            String nationality = body.getNationality();
            Integer numberOfPlayers = body.getNumberOfPlayers();

            if (nationality == null || numberOfPlayers == null) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "error", "Invalid input"));
            }

        try {
            playersInitializer.generatePlayersData(nationality, numberOfPlayers, Optional.empty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(Map.of("ok", true, "message", "Players generated successfully"));
    }

}

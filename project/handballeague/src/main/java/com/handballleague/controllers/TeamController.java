package com.handballleague.controllers;

import com.handballleague.model.Team;
import com.handballleague.services.JWTService;
import com.handballleague.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/teams")
public class TeamController {
    private final TeamService teamService;
    private final JWTService jwtService;

    @Autowired
    public TeamController(TeamService teamService, JWTService jwtService) {
        this.teamService = teamService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {

        List<Team> teams = teamService.getAll();
        return ResponseEntity.ok().body(Map.of("response", teams,
                "ok", true));

    }

    @PostMapping
    public ResponseEntity<?> registerNewTeam(@Valid @RequestBody Team team, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            Team newTeam = teamService.create(team);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Team created successfully", "response", newTeam));
        } else {
            return response;
        }
    }

    @PostMapping("/{teamId}/players")
    public ResponseEntity<?> addPlayerToTeam(@PathVariable Long teamId, @RequestBody Long playerId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            if (playerId == null || teamId == null) {
                return ResponseEntity.badRequest().build();
            }
            Team updatedTeam = teamService.addPlayerToTeam(teamId, playerId);
            return ResponseEntity.ok(updatedTeam);
        } else {
            return response2;
        }
    }

    @DeleteMapping("/{teamId}/players")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable Long teamId, @RequestBody Long playerId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            if (playerId == null || teamId == null) {
                return ResponseEntity.badRequest().build();
            }
            Team updatedTeam = teamService.removePlayerFromTeam(teamId, playerId);
            return ResponseEntity.ok(updatedTeam);
        } else {
            return response2;
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable("teamId") Long id, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            teamService.delete(id);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Team deleted successfully"));
        } else {
            return response;
        }
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamById(@PathVariable Long teamId) {

        Team team = teamService.getById(teamId);
        return ResponseEntity.ok(Map.of("ok", true, "response", team));

    }

    @PutMapping("/{teamId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long teamId, @Valid @RequestBody Team team, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Team newTeam = teamService.update(teamId, team);
            return ResponseEntity.ok(Map.of("ok", true, "response", newTeam));
        } else {
            return response2;
        }
    }
}

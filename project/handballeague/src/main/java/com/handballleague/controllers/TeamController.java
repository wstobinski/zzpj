package com.handballleague.controllers;

import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/teams")
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {
        try {
            List<Team> teams = teamService.getAll();
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> registerNewTeam(@RequestBody Team team) {
        try {
            teamService.create(team);
            return ResponseEntity.ok("Team created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{teamId}/players")
    public ResponseEntity<?> addPlayerToTeam(@PathVariable Long teamId, @RequestBody Long playerId) {
        if (playerId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Team updatedTeam = teamService.addPlayerToTeam(teamId, playerId);
            return ResponseEntity.ok(updatedTeam);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}/players")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable Long teamId, @RequestBody Long playerId) {
        if (playerId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Team updatedTeam = teamService.removePlayerFromTeam(teamId, playerId);
            return ResponseEntity.ok(updatedTeam);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable("teamId") Long id) {
        try {
            teamService.delete(id);
            return ResponseEntity.ok("Team deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamById(@PathVariable Long teamId) {
        try {
            Team team = teamService.getById(teamId);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long teamId, @RequestBody Team team) {
        try {
            Team newTeam = teamService.update(teamId, team);
            return ResponseEntity.ok(newTeam);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

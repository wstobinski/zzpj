package com.handballleague.controllers;

import com.handballleague.model.Team;
import com.handballleague.services.TeamService;
import jakarta.validation.Valid;
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

        List<Team> teams = teamService.getAll();
        return ResponseEntity.ok(teams);

    }

    @PostMapping
    public ResponseEntity<?> registerNewTeam(@Valid @RequestBody Team team) {

        teamService.create(team);
        return ResponseEntity.ok("Team created successfully");

    }

    @PostMapping("/{teamId}/players")
    public ResponseEntity<?> addPlayerToTeam(@PathVariable Long teamId, @RequestBody Long playerId) {
        if (playerId == null || teamId == null) {
            return ResponseEntity.badRequest().build();
        }

        Team updatedTeam = teamService.addPlayerToTeam(teamId, playerId);
        return ResponseEntity.ok(updatedTeam);

    }

    @DeleteMapping("/{teamId}/players")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable Long teamId, @RequestBody Long playerId) {
        if (playerId == null || teamId == null) {
            return ResponseEntity.badRequest().build();
        }

        Team updatedTeam = teamService.removePlayerFromTeam(teamId, playerId);
        return ResponseEntity.ok(updatedTeam);

    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable("teamId") Long id) {

        teamService.delete(id);
        return ResponseEntity.ok("Team deleted successfully");

    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamById(@PathVariable Long teamId) {

        Team team = teamService.getById(teamId);
        return ResponseEntity.ok(team);

    }

    @PutMapping("/{teamId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long teamId, @Valid @RequestBody Team team) {

        Team newTeam = teamService.update(teamId, team);
        return ResponseEntity.ok(newTeam);

    }
}

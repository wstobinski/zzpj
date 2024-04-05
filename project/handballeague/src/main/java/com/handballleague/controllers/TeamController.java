package com.handballleague.controllers;

import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Team> getTeams() {
        return teamService.getTeams();
    }

    @PostMapping
    public void registerNewTeam(@RequestBody Team team) {
        teamService.addNewTeam(team);
    }

    @PostMapping("/{teamId}/players")
    public ResponseEntity<Team> addPlayerToTeam(@PathVariable Long teamId, @RequestBody Long playerId) {
        if (playerId == null) {
            return ResponseEntity.badRequest().build();
        }
        Team updatedTeam = teamService.addPlayerToTeam(teamId, playerId);
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping(path = "/{teamId}")
    public void deleteTeam(@PathVariable("teamId") Long id) {
        teamService.deleteTeam(id);
    }


}

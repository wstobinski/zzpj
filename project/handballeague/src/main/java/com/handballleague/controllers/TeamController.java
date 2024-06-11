package com.handballleague.controllers;

import com.handballleague.DTO.GenerateTeamsDTO;
import com.handballleague.initialization.PlayersInitializer;
import com.handballleague.initialization.TeamsInitializer;
import com.handballleague.model.Team;
import com.handballleague.services.JWTService;
import com.handballleague.services.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/teams")
public class TeamController {
    private final TeamService teamService;

    private final JWTService jwtService;

    private final TeamsInitializer teamsInitializer;

    private final PlayersInitializer playersInitializer;

    @Autowired
    public TeamController(TeamService teamService, JWTService jwtService, TeamsInitializer teamsInitializer, PlayersInitializer playersInitializer) {
        this.teamService = teamService;
        this.jwtService = jwtService;
        this.teamsInitializer = teamsInitializer;
        this.playersInitializer = playersInitializer;
    }

    @GetMapping
    public ResponseEntity<?> getTeams() {
        List<Team> teams = teamService.getAll();
        return ResponseEntity.ok().body(Map.of("response", teams,
                "ok", true));

    }

    @GetMapping("/free-agents")
    public ResponseEntity<?> getFreeAgents() {
        List<Team> teams = teamService.getFreeAgents();
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

    @GetMapping("/{teamId}/get-all-players")
    public ResponseEntity<?> getAllPlayersForTeam(@PathVariable Long teamId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(teamService.getAllPlayers(teamId));
        } else {
            return response2;
        }
    }

    @PostMapping("/generate-teams")
    public ResponseEntity<?> generateTeams(@RequestBody GenerateTeamsDTO body, @RequestHeader(name = "Authorization") String token) throws Exception {

            ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
            if (!response.getStatusCode().is2xxSuccessful()) {
                return response;
            }

            String leagueId = body.getLeagueId();
            String season = body.getSeason();
            boolean generatePlayers = body.isGeneratePlayers();
            System.out.println("generatePlayers: " + generatePlayers);

            if (leagueId == null || season == null) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "error", "Invalid input"));
            }

            List<Long> teamsIDs =  teamsInitializer.fetchAndFillData(leagueId, season);

            System.out.println("Teams IDs");
            for (Long teamId : teamsIDs) {
                System.out.println("Team ID: " + teamId);
            }

            if (generatePlayers) {
                System.out.println("Generating players");
                playersInitializer.generatePlayersData("Polish", 6, Optional.of(teamsIDs));
            }

            return ResponseEntity.ok(Map.of("ok", true, "message", "Teams generated successfully"));
        }
    }


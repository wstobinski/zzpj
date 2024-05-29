package com.handballleague.controllers;

import com.handballleague.model.Match;
import com.handballleague.model.Player;
import com.handballleague.model.Referee;
import com.handballleague.model.Team;
import com.handballleague.services.JWTService;
import com.handballleague.services.MatchService;
import com.handballleague.services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth-check")
public class AuthCheckController {

    private final MatchService matchService;

    private final JWTService jwtService;

    private final TeamService teamService;

    public AuthCheckController(MatchService matchService, JWTService jwtService, TeamService teamService) {
        this.matchService = matchService;
        this.jwtService = jwtService;
        this.teamService = teamService;
    }

    @GetMapping("/is-referee-in-match/{matchId}")
    public ResponseEntity<?> isRefereeInMatch(@PathVariable Long matchId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "arbiter");
        if (response.getStatusCode().is2xxSuccessful()) {
            Match match = matchService.getById(matchId);
            Referee modelReferee = (Referee) jwtService.tokenToModel(token);
            if (match.getReferee().equals(modelReferee)) {
                return ResponseEntity.ok(Map.of("ok", true, "message", "Referee is assigned to this match"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Referee is not assigned to this match"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Unauthorized action"));
    }

    @GetMapping("/is-captain-in-match/{matchId}")
    public ResponseEntity<?> isCaptainInMatch(@PathVariable Long matchId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful()) {
            Player captain = (Player) jwtService.tokenToModel(token);
            Match match = matchService.getById(matchId);
            boolean isCaptainOfHomeTeam = match.getHomeTeam().getPlayers().stream()
                    .anyMatch(player -> player.equals(captain) && player.isCaptain());
            boolean isCaptainOfAwayTeam = match.getAwayTeam().getPlayers().stream()
                    .anyMatch(player -> player.equals(captain) && player.isCaptain());
            if (isCaptainOfHomeTeam || isCaptainOfAwayTeam) {
                return ResponseEntity.ok(Map.of("ok", true, "message", "Captain is part of this match"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Captain is not part of this match"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Unauthorized action"));
    }

    @GetMapping("/is-captain-of-team/{teamId}")
    public ResponseEntity<?> isCaptainOfTeam(@PathVariable Long teamId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful()) {
            Player captain = (Player) jwtService.tokenToModel(token);
            Team team = teamService.getById(teamId);
            boolean isCaptainOfTeam = team.getPlayers().stream()
                    .anyMatch(player -> player.equals(captain) && player.isCaptain());
            if (isCaptainOfTeam) {
                return ResponseEntity.ok(Map.of("ok", true, "message", "Player is captain of this team"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Player is not captain of this team"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Unauthorized action"));
    }
}

package com.handballleague.controllers;

import com.handballleague.DTO.GenerateScheduleDTO;
import com.handballleague.model.League;
import com.handballleague.model.Round;
import com.handballleague.services.JWTService;
import com.handballleague.services.LeagueService;
import com.handballleague.services.RoundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/leagues")
public class LeagueController {
    private final LeagueService leagueService;
    private final RoundService roundService;
    private final JWTService jwtService;

    @Autowired
    public LeagueController(LeagueService leagueService, RoundService roundService, JWTService jwtService) {
        this.leagueService = leagueService;
        this.roundService = roundService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getLeagues() {

        List<League> leagues = leagueService.getAll();
        return ResponseEntity.ok(Map.of("ok", true, "response", leagues));

    }

    @GetMapping("/{leagueId}/rounds")
    public ResponseEntity<?> getLeagueRounds(@PathVariable Long leagueId) {
        List<Round> rounds = roundService.getByLeagueId(leagueId);
        return ResponseEntity.ok(rounds);
    }

    @PostMapping
    public ResponseEntity<?> registerNewLeague(@Valid @RequestBody League league, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            leagueService.create(league);
            return ResponseEntity.ok(Map.of("ok", true, "message", "League successfully registered"));
        } else {
            return response;
        }
    }

    @PostMapping("/{leagueId}/teams")
    public ResponseEntity<?> addTeamToLeague(@PathVariable Long leagueId, @RequestBody Long teamId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            if (teamId == null) {
                return ResponseEntity.badRequest().build();
            }
            League updatedLeague = leagueService.addLeagueToTeam(leagueId, teamId);
            return ResponseEntity.ok(updatedLeague);
        } else {
            return response;
        }
    }

    @PostMapping("/{leagueId}/generate-schedule")
    public ResponseEntity<?> generateScheduleForLeague(@PathVariable Long leagueId, @RequestBody GenerateScheduleDTO generateScheduleDTO, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            League league = leagueService.getById(leagueId);
            leagueService.generateSchedule(league, generateScheduleDTO);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Schedule generated successfully"));
        } else {
            return response;
        }
    }

    @DeleteMapping("/{leagueId}/teams")
    public ResponseEntity<?> removeTeamFromLeague(@PathVariable Long leagueId, @RequestBody Long teamId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            if (teamId == null) {
                return ResponseEntity.badRequest().build();
            }

            League updatedLeague = leagueService.removeTeamFromLeague(leagueId, teamId);
            return ResponseEntity.ok(updatedLeague);
        } else {
            return response;
        }
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<?> deleteLeague(@PathVariable("leagueId") Long id, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            leagueService.delete(id);
            return ResponseEntity.ok(Map.of("ok", true, "message", "League successfully deleted"));
        } else {
            return response;
        }
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<?> getLeagueById(@PathVariable Long leagueId) {

        League league = leagueService.getById(leagueId);
        return ResponseEntity.ok(league);

    }

    @PutMapping("/{leagueId}")
    public ResponseEntity<?> updateLeague(@PathVariable Long leagueId, @Valid @RequestBody League league, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "arbiter");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            League newLeague = leagueService.update(leagueId, league);
            return ResponseEntity.ok(Map.of("ok", true, "response", newLeague));
        } else {
            return response2;
        }
    }

    @GetMapping("/{leagueId}/matches")
    public ResponseEntity<?> getMatchesForLeague(@PathVariable Long leagueId, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "arbiter");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of("ok", true, "response", leagueService.getAllMatchesInLeague(leagueId)));
        } else {
            return response2;
        }
    }

    @PatchMapping("/finish/{league_uuid}")
    public ResponseEntity<?> finishLeague(@PathVariable Long league_uuid, @RequestHeader(name = "Authorization") String token) throws RuntimeException {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "arbiter");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of("ok", true, "response", leagueService.finishLeague(league_uuid)));
        } else {
            return response2;
        }
    }
}

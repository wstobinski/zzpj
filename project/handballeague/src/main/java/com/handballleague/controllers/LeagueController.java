package com.handballleague.controllers;

import com.handballleague.model.League;
import com.handballleague.services.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/leagues")
public class LeagueController {
    private final LeagueService leagueService;

    @Autowired
    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public ResponseEntity<?> getLeagues() {
        try {
            List<League> leagues = leagueService.getAll();
            return ResponseEntity.ok(leagues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> registerNewLeague(@RequestBody League league) {
        try {
            leagueService.create(league);
            return ResponseEntity.ok("League created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{leagueId}/teams")
    public ResponseEntity<?> addTeamToLeague(@PathVariable Long leagueId, @RequestBody Long teamId) {
        if (teamId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            League updatedLeague = leagueService.addLeagueToTeam(leagueId, teamId);
            return ResponseEntity.ok(updatedLeague);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{leagueId}/teams")
    public ResponseEntity<?> removeTeamFromLeague(@PathVariable Long leagueId, @RequestBody Long teamId) {
        if (teamId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            League updatedLeague = leagueService.removeTeamFromLeague(leagueId, teamId);
            return ResponseEntity.ok(updatedLeague);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<?> deleteTeam(@PathVariable("leagueId") Long id) {
        try {
            leagueService.delete(id);
            return ResponseEntity.ok("League deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<?> getTeamById(@PathVariable Long leagueId) {
        try {
            League league = leagueService.getById(leagueId);
            return ResponseEntity.ok(league);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{leagueId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long leagueId, @RequestBody League league) {
        try {
            League newLeague = leagueService.update(leagueId, league);
            return ResponseEntity.ok(newLeague);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

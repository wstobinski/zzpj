package com.handballleague.controllers;

import com.handballleague.model.League;
import com.handballleague.services.LeagueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

        List<League> leagues = leagueService.getAll();
        return ResponseEntity.ok(leagues);

    }

    @PostMapping
    public ResponseEntity<?> registerNewLeague(@Valid @RequestBody League league) {

        leagueService.create(league);
        return ResponseEntity.ok("League created successfully");

    }

    @PostMapping("/{leagueId}/teams")
    public ResponseEntity<?> addTeamToLeague(@PathVariable Long leagueId, @RequestBody Long teamId) {
        if (teamId == null) {
            return ResponseEntity.badRequest().build();
        }
        League updatedLeague = leagueService.addLeagueToTeam(leagueId, teamId);
        return ResponseEntity.ok(updatedLeague);

    }

    @DeleteMapping("/{leagueId}/teams")
    public ResponseEntity<?> removeTeamFromLeague(@PathVariable Long leagueId, @RequestBody Long teamId) {
        if (teamId == null) {
            return ResponseEntity.badRequest().build();
        }

        League updatedLeague = leagueService.removeTeamFromLeague(leagueId, teamId);
        return ResponseEntity.ok(updatedLeague);

    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<?> deleteTeam(@PathVariable("leagueId") Long id) {

        leagueService.delete(id);
        return ResponseEntity.ok("League deleted successfully");

    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<?> getTeamById(@PathVariable Long leagueId) {

        League league = leagueService.getById(leagueId);
        return ResponseEntity.ok(league);

    }

    @PutMapping("/{leagueId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long leagueId, @Valid @RequestBody League league) {

        League newLeague = leagueService.update(leagueId, league);
        return ResponseEntity.ok(newLeague);

    }
}

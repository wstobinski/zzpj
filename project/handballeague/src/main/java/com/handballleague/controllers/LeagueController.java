package com.handballleague.controllers;

import com.handballleague.model.League;
import com.handballleague.model.Round;
import com.handballleague.services.LeagueService;
import com.handballleague.services.RoundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/leagues")
public class LeagueController {
    private final LeagueService leagueService;
    private final RoundService roundService;

    @Autowired
    public LeagueController(LeagueService leagueService, RoundService roundService) {
        this.leagueService = leagueService;
        this.roundService =roundService;
    }

    @GetMapping
    public ResponseEntity<?> getLeagues() {

        List<League> leagues = leagueService.getAll();
        return ResponseEntity.ok(leagues);

    }

    @GetMapping("/{leagueId}/rounds")
    public ResponseEntity<?> getLeagueRounds(@PathVariable Long leagueId) {
        List<Round> rounds = roundService.getByLeagueId(leagueId);
        return ResponseEntity.ok(rounds);
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

    @PostMapping("/{leagueId}/generate-schedule")
    public ResponseEntity<?> generateScheduleForLeague(@PathVariable Long leagueId, @RequestParam int rounds) {
        League league = leagueService.getById(leagueId);
        leagueService.generateSchedule(league, rounds);
        return ResponseEntity.ok("Schedule generated successfully");

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

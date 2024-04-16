package com.handballleague.controllers;

import com.handballleague.model.TeamContest;
import com.handballleague.services.TeamContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/team-contests")
public class TeamContestController {

    private final TeamContestService teamContestService;

    @Autowired
    public TeamContestController(TeamContestService teamContestService) {
        this.teamContestService = teamContestService;
    }
    @GetMapping
    public ResponseEntity<?> getTeamContests() {
        try {
            List<TeamContest> teamContests = teamContestService.getAll();
            return ResponseEntity.ok(teamContests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> registerNewTeamContest(@RequestParam Long leagueID, @RequestParam Long teamID) {
        try {
            teamContestService.create(leagueID, teamID);
            return ResponseEntity.ok("TeamContest created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamContestID}")
    public ResponseEntity<?> deleteTeamContest(@PathVariable Long teamContestID) {
        try {
            teamContestService.delete(teamContestID);
            return ResponseEntity.ok("TeamContest deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{teamContestID}")
    public ResponseEntity<?> getTeamContestByID(@PathVariable Long teamContestID) {
        try {
            TeamContest teamContest = teamContestService.getById(teamContestID);
            return ResponseEntity.ok(teamContest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{teamContestID}")
    public ResponseEntity<?> updateTeamContest(@PathVariable Long teamContestID, @RequestBody TeamContest teamContest) {
        try {
            TeamContest newTeamContest = teamContestService.update(teamContestID, teamContest);
            return ResponseEntity.ok(newTeamContest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

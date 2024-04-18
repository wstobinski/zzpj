package com.handballleague.controllers;

import com.handballleague.model.TeamContest;
import com.handballleague.services.TeamContestService;
import jakarta.validation.Valid;
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

        List<TeamContest> teamContests = teamContestService.getAll();
        return ResponseEntity.ok(teamContests);

    }

    @PostMapping
    public ResponseEntity<?> registerNewTeamContest(@RequestParam Long leagueID, @RequestParam Long teamID) {

        teamContestService.create(leagueID, teamID);
        return ResponseEntity.ok("TeamContest created successfully");

    }

    @DeleteMapping("/{teamContestID}")
    public ResponseEntity<?> deleteTeamContest(@PathVariable Long teamContestID) {

        teamContestService.delete(teamContestID);
        return ResponseEntity.ok("TeamContest deleted successfully");

    }

    @GetMapping("/{teamContestID}")
    public ResponseEntity<?> getTeamContestByID(@PathVariable Long teamContestID) {

        TeamContest teamContest = teamContestService.getById(teamContestID);
        return ResponseEntity.ok(teamContest);

    }

    @PutMapping("/{teamContestID}")
    public ResponseEntity<?> updateTeamContest(@PathVariable Long teamContestID, @Valid @RequestBody TeamContest teamContest) {

        TeamContest newTeamContest = teamContestService.update(teamContestID, teamContest);
        return ResponseEntity.ok(newTeamContest);

    }

}

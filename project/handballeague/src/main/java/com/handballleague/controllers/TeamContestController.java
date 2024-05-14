package com.handballleague.controllers;

import com.handballleague.model.TeamContest;
import com.handballleague.services.JWTService;
import com.handballleague.services.TeamContestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/team-contests")
public class TeamContestController {

    private final TeamContestService teamContestService;
    private final JWTService jwtService;

    @Autowired
    public TeamContestController(TeamContestService teamContestService, JWTService jwtService) {
        this.teamContestService = teamContestService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getTeamContests() {

        List<TeamContest> teamContests = teamContestService.getAll();
        return ResponseEntity.ok(teamContests);

    }


    @PostMapping
    public ResponseEntity<?> registerNewTeamContest(@RequestParam Long leagueID, @RequestParam Long teamID,
                                                    @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            teamContestService.create(leagueID, teamID);
            return ResponseEntity.ok("TeamContest created successfully");
        } else {
            return response;
        }

    }

    @DeleteMapping("/{teamContestID}")
    public ResponseEntity<?> deleteTeamContest(@PathVariable Long teamContestID, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            teamContestService.delete(teamContestID);
            return ResponseEntity.ok("TeamContest deleted successfully");
        } else {
            return response;
        }
    }

    @GetMapping("/{teamContestID}")
    public ResponseEntity<?> getTeamContestByID(@PathVariable Long teamContestID) {

        TeamContest teamContest = teamContestService.getById(teamContestID);
        return ResponseEntity.ok(teamContest);

    }

    @PutMapping("/{teamContestID}")
    public ResponseEntity<?> updateTeamContest(@PathVariable Long teamContestID, @Valid @RequestBody TeamContest teamContest,
                                               @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful()) {
            TeamContest newTeamContest = teamContestService.update(teamContestID, teamContest);
            return ResponseEntity.ok(newTeamContest);
        } else {
            return response;
        }
    }

    @GetMapping("/for-league/{leagueId}")
    public ResponseEntity<?> getTeamContestsForLeague(@PathVariable Long leagueId) {
        List<TeamContest> teamContests = teamContestService.findTeamContestsInCertainLeague(leagueId);
        return ResponseEntity.ok(Map.of("ok", true, "response", teamContests));
    }

}

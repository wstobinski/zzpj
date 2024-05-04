package com.handballleague.controllers;

import com.handballleague.DTO.MatchScoreDTO;
import com.handballleague.model.Match;
import com.handballleague.model.Team;
import com.handballleague.services.JWTService;
import com.handballleague.services.LeagueService;
import com.handballleague.services.MatchService;
import com.handballleague.services.RoundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/matches")
public class MatchController {
    private final MatchService matchService;
    private final JWTService jwtService;

    public MatchController(MatchService matchService, JWTService jwtService) {
        this.matchService = matchService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getAllMatches(@RequestHeader(name = "Authorization") String token){
        List<Match> matches = matchService.getAll();
        return ResponseEntity.ok().body(Map.of("response", matches,
                "ok", true));
    }

    @PostMapping("/{matchId}/finish-match")
    public ResponseEntity<?> completeMatch(@PathVariable Long matchId, @RequestBody MatchScoreDTO.MatchResultDto matchResult) {
        try {
            matchService.endMatch(matchId, matchResult);
            return ResponseEntity.ok().body(Map.of("message", "Match finished successfully","response", matchResult,
                    "ok", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error completing match: " + e.getMessage());
        }
    }
}

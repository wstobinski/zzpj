package com.handballleague.controllers;

import com.handballleague.DTO.MatchScoreDTO;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Match;
import com.handballleague.model.Score;
import com.handballleague.model.Team;
import com.handballleague.repositories.ScoreRepository;
import com.handballleague.services.JWTService;
import com.handballleague.services.LeagueService;
import com.handballleague.services.MatchService;
import com.handballleague.services.RoundService;
import jakarta.validation.Valid;
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
    private final ScoreRepository scoreRepository;

    public MatchController(MatchService matchService, JWTService jwtService, ScoreRepository scoreRepository) {
        this.matchService = matchService;
        this.jwtService = jwtService;
        this.scoreRepository = scoreRepository;
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

    @PutMapping("/{matchId}")
    public ResponseEntity<?> updateMatch(@PathVariable Long matchId, @Valid @RequestBody Match match, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Match newMatch = matchService.update(matchId, match);
            return ResponseEntity.ok(Map.of("ok", true, "response", newMatch));
        } else {
            return response2;
        }
    }

    @GetMapping("score/{matchId}")
    public ResponseEntity<?> getMatchScores(@PathVariable Long matchId) {

        List<Score> scores = scoreRepository.findByMatch(matchService.getById(matchId)).orElseThrow(() -> new ObjectNotFoundInDataBaseException("Match not found"));
        return ResponseEntity.ok(Map.of("ok", true, "response", scores));

    }
}

package com.handballleague.controllers;

import com.handballleague.DTO.MatchScoreDTO;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Match;
import com.handballleague.model.Player;
import com.handballleague.model.Referee;
import com.handballleague.model.Score;
import com.handballleague.repositories.ScoreRepository;
import com.handballleague.services.JWTService;
import com.handballleague.services.MatchService;
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
    public ResponseEntity<?> getAllMatches(@RequestHeader(name = "Authorization") String token) {
        List<Match> matches = matchService.getAll();
        return ResponseEntity.ok().body(Map.of("response", matches,
                "ok", true));
    }

    @PostMapping("/{matchId}/finish-match")
    public ResponseEntity<?> completeMatch(@PathVariable Long matchId, @RequestBody MatchScoreDTO.MatchResultDto matchResult, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "arbiter");
        if (response.getStatusCode().is2xxSuccessful()) {
            matchService.endMatch(matchId, matchResult);
            return ResponseEntity.ok().body(Map.of("message", "Match finished successfully", "response", matchResult,
                    "ok", true));
        } else if (response2.getStatusCode().is2xxSuccessful()) {
            Match match = matchService.getById(matchId);
            Referee modelReferee = (Referee) jwtService.tokenToModel(token);
            if (match.getReferee().equals(modelReferee)) {
                matchService.endMatch(matchId, matchResult);
                return ResponseEntity.ok().body(Map.of("message", "Match finished successfully", "response", matchResult,
                        "ok", true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false,"message", "This referee is not authorized to perform this operation"));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of( "ok", false, "message", "Unauthorized action"));

    }

    @PutMapping("/{matchId}")
    public ResponseEntity<?> updateMatch(@PathVariable Long matchId, @Valid @RequestBody Match match, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> responseAdmin = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> responseCaptain = jwtService.handleAuthorization(token, "captain");
        ResponseEntity<?> responseReferee = jwtService.handleAuthorization(token, "arbiter");

        if (responseAdmin.getStatusCode().is2xxSuccessful()) {
            Match newMatch = matchService.update(matchId, match);
            return ResponseEntity.ok(Map.of("ok", true, "response", newMatch));
        } else if (responseCaptain.getStatusCode().is2xxSuccessful()) {
            Player captain = (Player) jwtService.tokenToModel(token);
            Match currentMatch = matchService.getById(matchId);

            boolean isCaptainOfHomeTeam = currentMatch.getHomeTeam().getPlayers().stream()
                    .anyMatch(player -> player.equals(captain) && player.isCaptain());

            boolean isCaptainOfAwayTeam = currentMatch.getAwayTeam().getPlayers().stream()
                    .anyMatch(player -> player.equals(captain) && player.isCaptain());

            if (isCaptainOfHomeTeam || isCaptainOfAwayTeam) {
                Match newMatch = matchService.update(matchId, match);
                return ResponseEntity.ok(Map.of("ok", true, "response", newMatch));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "This captain is not authorized to perform this operation"));
            }
        } else if (responseReferee.getStatusCode().is2xxSuccessful()) {
            Match existingMatch = matchService.getById(matchId);
            Referee modelReferee = (Referee) jwtService.tokenToModel(token);
            if (existingMatch.getReferee().equals(modelReferee)) {
                Match newMatch = matchService.update(matchId, match);
                return ResponseEntity.ok(Map.of("ok", true, "response", newMatch));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "This referee is not authorized to perform this operation"));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("ok", false, "message", "Unauthorized action"));
    }



    @GetMapping("score/{matchId}")
    public ResponseEntity<?> getMatchScores(@PathVariable Long matchId) {

        List<Score> scores = scoreRepository.findByMatch(matchService.getById(matchId)).orElseThrow(() -> new ObjectNotFoundInDataBaseException("Match not found"));
        return ResponseEntity.ok(Map.of("ok", true, "response", scores));

    }
}

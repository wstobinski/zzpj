package com.handballleague.controllers;

import com.handballleague.model.Player;
import com.handballleague.model.Referee;
import com.handballleague.services.JWTService;
import com.handballleague.services.PlayerService;
import com.handballleague.services.RefereeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Ref;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/referees")
public class RefereeController {
    private final RefereeService refereeService;
    private final JWTService jwtService;
    @Autowired
    public RefereeController(RefereeService refereeService, JWTService jwtService) {
        this.refereeService = refereeService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getReferees() {
        List<Referee> referees = refereeService.getAll();
        return ResponseEntity.ok().body(Map.of("response", referees,
                "ok", true));
    }

    //todo: IMPLEMENT FINDING FREE REFEREES
    @GetMapping("/free-agents")
    public ResponseEntity<?> getFreeAgents() {
        List<Referee> referees= refereeService.getAll();
        return ResponseEntity.ok().body(Map.of("response", referees,
                "ok", true));

    }

    @PostMapping()
    public ResponseEntity<?> registerNewReferee(@Valid @RequestBody Referee referee, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Referee newReferee = refereeService.create(referee);
            return ResponseEntity.ok().body(Map.of("message", "Player created successfully",
                    "response", newReferee,
                    "ok", true));
        } else {
            return response2;
        }
    }

    @DeleteMapping(path = "/{refereeId}")
    public ResponseEntity<?> deletePlayer(@PathVariable("refereeId") Long id, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response1 = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response1.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            boolean deleted = refereeService.delete(id);
            return ResponseEntity.ok(Map.of("ok", deleted));
        } else {
            return response2;
        }
    }

    @GetMapping("/{refereeId}")
    public ResponseEntity<?> getRefereeById(@PathVariable Long refereeId) {
        Referee referee = refereeService.getById(refereeId);
        return ResponseEntity.ok(referee);
    }

    @PutMapping("/{refereeId}")
    public ResponseEntity<?> updateReferee(@Valid @PathVariable Long refereeId, @RequestBody Referee referee, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Referee newReferee = refereeService.update(refereeId, referee);
            return ResponseEntity.ok(Map.of("ok", true, "response", refereeId));
        } else {
            return response2;
        }
    }
}

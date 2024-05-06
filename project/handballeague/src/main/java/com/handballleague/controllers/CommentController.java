package com.handballleague.controllers;

import com.handballleague.DTO.CommentDTO;
import com.handballleague.model.*;
import com.handballleague.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/comments")
public class CommentController {
    private final CommentService commentService;
    private final MatchService matchService;
    private final RefereeService refereeService;
    private final PlayerService playerService;
    private final JWTService jwtService;

    @Autowired
    public CommentController(CommentService commentService, MatchService matchService, RefereeService refereeService, PlayerService playerService, JWTService jwtService) {
        this.commentService = commentService;
        this.matchService = matchService;
        this.refereeService = refereeService;
        this.playerService = playerService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getAllComments(@RequestHeader(name = "Authorization") String token){
        List<Comment> comments = commentService.getAll();
        return ResponseEntity.ok().body(Map.of("response", comments,
                "ok", true));
    }

    @PostMapping
    public ResponseEntity<?> addNewComment(@Valid @RequestBody CommentDTO commentDTO, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "captain");
        //TODO: Poźniej usunąć admina, zeby tylko kapitan mógł dodawać komentarze
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Comment comment = convertDTOToEntity(commentDTO);
            commentService.create(comment);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Comment added successfully.", "response", comment));
        } else {
            return response;
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@Valid @RequestBody Comment comment, @RequestHeader(name = "Authorization") String token, @PathVariable Long commentId) {
        ResponseEntity<?> response = jwtService.handleAuthorization(token, "captain");
        //TODO: Poźniej usunąć admina, zeby tylko kapitan mógł dodawać komentarze
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "admin");
        if (response.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            Comment newComment = commentService.update(commentId, comment);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Comment added successfully.", "response", newComment));
        } else {
            return response;
        }
    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long id, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> response1 = jwtService.handleAuthorization(token, "admin");
        ResponseEntity<?> response2 = jwtService.handleAuthorization(token, "captain");
        if (response1.getStatusCode().is2xxSuccessful() || response2.getStatusCode().is2xxSuccessful()) {
            boolean deleted = commentService.delete(id);
            return ResponseEntity.ok(Map.of("ok", deleted));
        } else {
            return response2;
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getById(commentId);
        return ResponseEntity.ok(comment);
    }

    private Comment convertDTOToEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        Match match = matchService.getById(commentDTO.getMatchId());
        Referee referee = refereeService.getById(commentDTO.getRefereeId());
        Player author = playerService.getById(commentDTO.getAuthorId());

        comment.setMatch(match);
        comment.setReferee(referee);
        comment.setAuthor(author);
        comment.setCreatedDate(LocalDateTime.now());
        return comment;
    }
}

package com.handballleague.controllers;

import com.handballleague.DTO.CommentDTO;
import com.handballleague.DTO.PostDTO;
import com.handballleague.model.*;
import com.handballleague.services.JWTService;
import com.handballleague.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/posts")
public class PostController {
    private final PostService postService;
    private final JWTService jwtService;

    @Autowired
    public PostController(PostService postService, JWTService jwtService) {
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestHeader(name = "Authorization") String token){
        List<Post> posts = postService.getAll();
        return ResponseEntity.ok().body(Map.of("response", posts,
                "ok", true));
    }

    @PostMapping
    public ResponseEntity<?> addNewPost(@Valid @RequestBody PostDTO postDTO, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> responseAdmin = jwtService.handleAuthorization(token, "admin");
        if (responseAdmin.getStatusCode().is2xxSuccessful()) {
            Post post = convertDTOToEntity(postDTO);
            postService.create(post);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Post added successfully.", "response", post));
        } else {
            return responseAdmin;
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@Valid @RequestBody Post post, @RequestHeader(name = "Authorization") String token, @PathVariable Long postId) {
        ResponseEntity<?> responseAdmin = jwtService.handleAuthorization(token, "admin");
        if (responseAdmin.getStatusCode().is2xxSuccessful()) {
            Post newPost = postService.update(postId, post);
            return ResponseEntity.ok(Map.of("ok", true, "message", "Comment added successfully.", "response", newPost));
        } else {
            return responseAdmin;
        }
    }

    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<?> deleteComment(@PathVariable("postId") Long id, @RequestHeader(name = "Authorization") String token) {
        ResponseEntity<?> responseAdmin = jwtService.handleAuthorization(token, "admin");
        if (responseAdmin.getStatusCode().is2xxSuccessful()) {
            boolean deleted = postService.delete(id);
            return ResponseEntity.ok(Map.of("ok", deleted));
        } else {
            return responseAdmin;
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long postId) {
        Post post = postService.getById(postId);
        return ResponseEntity.ok(Map.of("ok", post));
    }

    private Post convertDTOToEntity(PostDTO postDTO) {
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setTitle(postDTO.getTitle());
        post.setPostedDate(postDTO.getPostedDate());

        return post;
    }
}

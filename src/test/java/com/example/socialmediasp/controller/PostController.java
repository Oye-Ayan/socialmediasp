package com.example.socialmediasp.controller;

import com.example.socialmediasp.dto.request.CommentRequest;
import com.example.socialmediasp.dto.request.PostRequest;
import com.example.socialmediasp.dto.request.SearchRequest;
import com.example.socialmediasp.entity.Comment;
import com.example.socialmediasp.entity.Post;
import com.example.socialmediasp.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Post management APIs")
public class PostController {

    private final PostService postService;

    // ─── CREATE POST ───────────────────────────────────────────
    @PostMapping
    @Operation(summary = "Create a new post",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Post> createPost(
            @Valid @RequestBody PostRequest request,
            Authentication auth) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(request, auth.getName()));
    }

    // ─── GET ALL POSTS ─────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Get all posts with pagination",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy) {
        return ResponseEntity.ok(
                postService.getAllPosts(page, size, sortBy));
    }

    // ─── GET POST BY ID ────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Get a post by ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // ─── UPDATE POST ───────────────────────────────────────────
    @PutMapping("/{id}")
    @Operation(summary = "Update a post (owner only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            Authentication auth) {
        return ResponseEntity.ok(
                postService.updatePost(id, request, auth.getName()));
    }

    // ─── DELETE POST ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post (owner only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            Authentication auth) {
        postService.deletePost(id, auth.getName());
        return ResponseEntity.ok("Post deleted successfully");
    }

    // ─── ADD COMMENT ───────────────────────────────────────────
    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a post",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Comment> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication auth) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.addComment(id, request, auth.getName()));
    }

    // ─── LIKE / UNLIKE POST ────────────────────────────────────
    @PostMapping("/{id}/like")
    @Operation(summary = "Like or unlike a post",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> likePost(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(
                postService.likeOrUnlikePost(id, auth.getName()));
    }

    // ─── SEARCH POSTS ──────────────────────────────────────────
    @PostMapping("/search")
    @Operation(summary = "Search posts by keyword",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<Post>> searchPosts(
            @Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(postService.searchPosts(request));
    }
}
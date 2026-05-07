package com.example.socialmediasp.controller;

import com.example.socialmediasp.dto.request.LoginRequest;
import com.example.socialmediasp.dto.request.RegisterRequest;
import com.example.socialmediasp.dto.request.SearchRequest;
import com.example.socialmediasp.dto.response.AuthResponse;
import com.example.socialmediasp.entity.User;
import com.example.socialmediasp.service.UserService;
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

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    // ─── REGISTER ──────────────────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    // ─── LOGIN ─────────────────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ─── GET USER BY ID ────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ─── FOLLOW USER ───────────────────────────────────────────
    @PostMapping("/{id}/follow")
    @Operation(summary = "Follow a user",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> followUser(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(
                userService.followUser(id, auth.getName()));
    }

    // ─── UNFOLLOW USER ─────────────────────────────────────────
    @DeleteMapping("/{id}/follow")
    @Operation(summary = "Unfollow a user",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> unfollowUser(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(
                userService.unfollowUser(id, auth.getName()));
    }

    // ─── GET FOLLOWERS ─────────────────────────────────────────
    @GetMapping("/{id}/followers")
    @Operation(summary = "Get all followers of a user",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<User>> getFollowers(
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getFollowers(id));
    }

    // ─── GET FOLLOWING ─────────────────────────────────────────
    @GetMapping("/{id}/following")
    @Operation(summary = "Get all users followed by a user",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<User>> getFollowing(
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getFollowing(id));
    }

    // ─── SEARCH USERS ──────────────────────────────────────────
    @PostMapping("/search")
    @Operation(summary = "Search users by username, email or bio",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<User>> searchUsers(
            @Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(userService.searchUsers(request));
    }
}
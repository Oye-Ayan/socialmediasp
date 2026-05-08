package com.example.socialmediasp.service;

import com.example.socialmediasp.dto.request.LoginRequest;
import com.example.socialmediasp.dto.request.RegisterRequest;
import com.example.socialmediasp.dto.request.SearchRequest;
import com.example.socialmediasp.dto.response.AuthResponse;
import com.example.socialmediasp.entity.Follow;
import com.example.socialmediasp.entity.User;
import com.example.socialmediasp.exception.ResourceNotFoundException;
// import com.example.socialmediasp.exception.UnauthorizedException;
import com.example.socialmediasp.repository.FollowRepository;
import com.example.socialmediasp.repository.UserRepository;
import com.example.socialmediasp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
@Transactional
public class UserService {

        private final UserRepository userRepository;
        private final FollowRepository followRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;

        // ─── REGISTER ──────────────────────────────────────────────
        public User register(RegisterRequest request) {

                if (userRepository.existsByUsername(request.getUsername()))
                        throw new RuntimeException("Username already taken");

                if (userRepository.existsByEmail(request.getEmail()))
                        throw new RuntimeException("Email already registered");

                User user = User.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .bio(request.getBio())
                                .profilePicture(request.getProfilePicture())
                                .build();

                return userRepository.save(user);
        }

        // ─── LOGIN ─────────────────────────────────────────────────
        public AuthResponse login(LoginRequest request) {

                // This throws automatically if credentials are wrong
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));

                User user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                String token = jwtUtil.generateToken(request.getUsername());

                return new AuthResponse(
                                token,
                                "Bearer",
                                user.getUsername(),
                                user.getId());
        }

        // ─── GET USER BY ID ────────────────────────────────────────
        @Cacheable(key = "#id")
        public User getUserById(Long id) {
                return userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        }

        // ─── FOLLOW USER ───────────────────────────────────────────
        public String followUser(Long targetUserId, String currentUsername) {

                User follower = userRepository.findByUsername(currentUsername)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (follower.getId().equals(targetUserId))
                        throw new RuntimeException("You cannot follow yourself");

                User following = userRepository.findById(targetUserId)
                                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

                if (followRepository.existsByFollowerIdAndFollowingId(
                                follower.getId(), following.getId()))
                        throw new RuntimeException("You are already following this user");

                Follow follow = Follow.builder()
                                .follower(follower)
                                .following(following)
                                .build();

                followRepository.save(follow);
                return "Successfully followed " + following.getUsername();
        }

        // ─── UNFOLLOW USER ─────────────────────────────────────────
        public String unfollowUser(Long targetUserId, String currentUsername) {

                User follower = userRepository.findByUsername(currentUsername)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Follow follow = followRepository
                                .findByFollowerIdAndFollowingId(follower.getId(), targetUserId)
                                .orElseThrow(() -> new RuntimeException("You are not following this user"));

                followRepository.delete(follow);
                return "Successfully unfollowed";
        }

        // ─── GET FOLLOWERS ─────────────────────────────────────────
        public List<User> getFollowers(Long userId) {
                // Make sure user exists first
                getUserById(userId);

                return followRepository.findByFollowingId(userId)
                                .stream()
                                .map(Follow::getFollower)
                                .collect(Collectors.toList());
        }

        // ─── GET FOLLOWING ─────────────────────────────────────────
        public List<User> getFollowing(Long userId) {
                // Make sure user exists first
                getUserById(userId);

                return followRepository.findByFollowerId(userId)
                                .stream()
                                .map(Follow::getFollowing)
                                .collect(Collectors.toList());
        }

        // ─── SEARCH USERS ──────────────────────────────────────────
        public Page<User> searchUsers(SearchRequest request) {
                Pageable pageable = PageRequest.of(
                                request.getPage(),
                                request.getSize());
                return userRepository
                                .findByUsernameContainingOrEmailContainingOrBioContaining(
                                                request.getKeyword(),
                                                request.getKeyword(),
                                                request.getKeyword(),
                                                pageable);
        }

        // ─── EVICT CACHE ───────────────────────────────────────────
        @CacheEvict(key = "#id")
        public void evictUserCache(Long id) {
                // Called when user data changes
        }
}
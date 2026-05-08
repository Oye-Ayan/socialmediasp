package com.example.socialmediasp.service;

import com.example.socialmediasp.dto.request.CommentRequest;
import com.example.socialmediasp.dto.request.PostRequest;
import com.example.socialmediasp.dto.request.SearchRequest;
import com.example.socialmediasp.entity.Comment;
import com.example.socialmediasp.entity.Post;
import com.example.socialmediasp.entity.User;
import com.example.socialmediasp.exception.ResourceNotFoundException;
import com.example.socialmediasp.exception.UnauthorizedException;
import com.example.socialmediasp.repository.CommentRepository;
import com.example.socialmediasp.repository.PostRepository;
import com.example.socialmediasp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // ─── HELPER: get current user ──────────────────────────────
    private User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    // ─── HELPER: get post by id ────────────────────────────────
    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Post not found with id: " + postId));
    }

    // ─── CREATE POST ───────────────────────────────────────────
    public Post createPost(PostRequest request, String username) {
        User user = getCurrentUser(username);

        Post post = Post.builder()
                .content(request.getContent())
                .user(user)
                .build();

        return postRepository.save(post);
    }

    // ─── GET ALL POSTS (paginated) ─────────────────────────────
    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(sortBy).descending()
        );
        return postRepository.findAll(pageable);
    }

    // ─── GET POST BY ID ────────────────────────────────────────
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return getPost(id);
    }

    // ─── UPDATE POST ───────────────────────────────────────────
    @Transactional
    public Post updatePost(Long postId, PostRequest request, String username) {
        Post post = getPost(postId);

        // Only owner can update
        if (!post.getUser().getUsername().equals(username))
            throw new UnauthorizedException(
                    "You are not allowed to update this post");

        post.setContent(request.getContent());
        return postRepository.save(post);
    }

    // ─── DELETE POST ───────────────────────────────────────────
    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = getPost(postId);

        // Only owner can delete
        if (!post.getUser().getUsername().equals(username))
            throw new UnauthorizedException(
                    "You are not allowed to delete this post");

        postRepository.delete(post);
    }

    // ─── ADD COMMENT ───────────────────────────────────────────
    public Comment addComment(Long postId,
                              CommentRequest request,
                              String username) {
        Post post = getPost(postId);
        User user = getCurrentUser(username);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .build();

        return commentRepository.save(comment);
    }

    // ─── LIKE / UNLIKE POST ────────────────────────────────────
    @Transactional
    public String likeOrUnlikePost(Long postId, String username) {
        Post post = getPost(postId);
        User user = getCurrentUser(username);

        boolean alreadyLiked = post.getLikes()
                .stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (alreadyLiked) {
            post.getLikes().removeIf(u -> u.getId().equals(user.getId()));
            postRepository.save(post);
            return "Post unliked successfully";
        } else {
            post.getLikes().add(user);
            postRepository.save(post);
            return "Post liked successfully";
        }
    }

    // ─── SEARCH POSTS ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(SearchRequest request) {
        String sortBy = request.getSortBy();
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "timestamp";
        }
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSortBy()).descending()
        );
        return postRepository.findByContentContainingIgnoreCase(
                request.getKeyword(),
                pageable
        );
    }
}
package com.example.socialmediasp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment cannot be empty")
    private String content;
}
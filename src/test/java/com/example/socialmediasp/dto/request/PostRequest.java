package com.example.socialmediasp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 2000, message = "Post content cannot exceed 2000 characters")
    private String content;
}
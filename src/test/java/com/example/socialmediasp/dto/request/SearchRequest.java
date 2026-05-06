package com.example.socialmediasp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequest {

    @NotBlank(message = "Keyword is required")
    private String keyword;

    private int page = 0;

    private int size = 10;

    private String sortBy = "timestamp";
}
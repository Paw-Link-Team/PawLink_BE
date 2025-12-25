package com.gdg.backend.review.dto;

public record ReviewUpdateRequest(
        int rating,
        String content
) {}

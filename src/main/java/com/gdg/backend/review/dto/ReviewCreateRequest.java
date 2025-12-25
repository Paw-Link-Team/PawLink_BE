package com.gdg.backend.review.dto;

public record ReviewCreateRequest(
        int rating,
        String content
) {}

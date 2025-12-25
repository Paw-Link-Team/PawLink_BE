package com.gdg.backend.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        String writerNickname,
        String writerProfileImageUrl,
        int rating,
        String content,
        LocalDateTime createdAt
) {}

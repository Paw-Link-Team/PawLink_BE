package com.gdg.backend.owner.dto;

import java.time.LocalDateTime;

public record OwnerProfileResponse(
        Long userId,
        String nickname,
        String profileImageUrl,
        int petCount,
        int reviewCount,
        LocalDateTime startedAt
) {}

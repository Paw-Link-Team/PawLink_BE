package com.gdg.backend.walker.dto;

import java.math.BigDecimal;

public record WalkerRankResponse(
        int rank,
        Long userId,
        String nickname,
        BigDecimal totalDistanceKm,
        int walkCount
) {
    public static WalkerRankResponse of(
            int rank,
            Long userId,
            String nickname,
            BigDecimal totalDistanceKm,
            int walkCount
    ) {
        return new WalkerRankResponse(
                rank,
                userId,
                nickname,
                totalDistanceKm,
                walkCount
        );
    }
}

package com.gdg.backend.walker.dto;

import java.math.BigDecimal;

public record WalkerProfileResponse(
        Long userId,
        String nickname,
        String location,
        String phoneNumber,
        double rating,
        BigDecimal totalDistanceKm,
        int walkCount,
        int careerYears,
        String profileImage
) {}

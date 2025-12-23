package com.gdg.backend.walkHistory.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WalkHistoryCreateRequest {
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal distanceKm;
}

package com.gdg.backend.walkHistory.dto;

import com.gdg.backend.walkHistory.domain.PoopStatus;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class WalkHistoryResponse {

    private Long id;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationSec;
    private BigDecimal distanceKm;
    private double avgSpeed;
    private String memo;
    private PoopStatus poop;

    public static WalkHistoryResponse from(WalkHistory walkHistory) {
        return WalkHistoryResponse.builder()
                .id(walkHistory.getId())
                .startedAt(walkHistory.getStartedAt())
                .endedAt(walkHistory.getEndedAt())
                .durationSec(walkHistory.getDurationSec())
                .distanceKm(walkHistory.getDistanceKm())
                .avgSpeed(walkHistory.getAvgSpeed())
                .memo(walkHistory.getMemo())
                .poop(walkHistory.getPoop())
                .build();
    }
}

package com.gdg.backend.walkHistory.dto;

import com.gdg.backend.walkHistory.domain.PoopStatus;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WalkHistoryCreateRequest {

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime endedAt;

    @NotNull
    @PositiveOrZero
    private BigDecimal distanceKm;

    private String memo;

    @NotNull
    private PoopStatus poop;

    @Builder
    public WalkHistoryCreateRequest(LocalDateTime startedAt, LocalDateTime endedAt, BigDecimal distanceKm, String memo, PoopStatus poop){
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.distanceKm = distanceKm;
        this.memo = memo;
        this.poop = poop;
    }
}

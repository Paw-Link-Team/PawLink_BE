package com.gdg.backend.walk.session.dto;

import com.gdg.backend.walkHistory.domain.PoopStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class WalkEndRequest {
    @NotNull
    @Positive
    private BigDecimal distanceKm;

    private String memo;

    @NotNull
    private PoopStatus poop;
}

package com.gdg.backend.walk.session.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class WalkEndRequest {
    private BigDecimal distanceKm;
}

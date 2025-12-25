package com.gdg.backend.walker.walkHistory.dto;

import com.gdg.backend.walker.walkHistory.domain.WalkHistory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WalkHistoryResponse {

    private Long id;
    private String date;
    private String duration;
    private BigDecimal distanceKm;
    private double averageSpeed;

    public static WalkHistoryResponse from(WalkHistory walkHistory) {
        return WalkHistoryResponse.builder()
                .id(walkHistory.getId())
                .date(formatDate(walkHistory.getStartedAt()))
                .duration(formatDuration(walkHistory.getDuration()))
                .distanceKm(walkHistory.getDistanceKm())
                .averageSpeed(
                        Math.round(
                                walkHistory.getAverageSpeedKmPerHour() * 10
                        ) / 10.0
                )
                .build();
    }

    private static String formatDate(java.time.LocalDateTime dateTime) {
        return dateTime.getYear() + ". "
                + dateTime.getMonthValue() + ". "
                + dateTime.getDayOfMonth();
    }

    private static String formatDuration(java.time.Duration duration) {
        long seconds = duration.getSeconds();
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;

        return String.format("%02d:%02d:%02d", h, m, s);
    }
}


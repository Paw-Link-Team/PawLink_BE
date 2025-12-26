package com.gdg.backend.walkHistory.dto;

import com.gdg.backend.walkHistory.domain.PoopStatus;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class WalkHistoryDetailResponse {

    private Long walkId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal distanceKm;
    private String memo;
    private PoopStatus poop;
    private List<String> imageUrls;

    public static WalkHistoryDetailResponse of(
            WalkHistory history,
            List<String> imageUrls
    ) {
        return new WalkHistoryDetailResponse(
                history.getId(),
                history.getStartedAt(),
                history.getEndedAt(),
                history.getDistanceKm(),
                history.getMemo(),
                history.getPoop(),
                imageUrls
        );
    }
}

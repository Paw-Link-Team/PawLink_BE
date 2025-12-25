package com.gdg.backend.walk.session.dto;

import com.gdg.backend.walk.session.domain.WalkSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WalkSessionStatusResponse {
    private boolean walking;
    private Long sessionId;
    private LocalDateTime startedAt;

    public static WalkSessionStatusResponse from(WalkSession session) {
        return new WalkSessionStatusResponse(
                true,
                session.getId(),
                session.getStartedAt()
        );
    }
}

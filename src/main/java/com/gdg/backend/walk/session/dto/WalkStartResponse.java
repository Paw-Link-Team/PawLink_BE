package com.gdg.backend.walk.session.dto;

import com.gdg.backend.walk.session.domain.WalkSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WalkStartResponse {
    private Long sessionId;
    private LocalDateTime startedAt;

    public static WalkStartResponse from(WalkSession session) {
        return new WalkStartResponse(
                session.getId(),
                session.getStartedAt()
        );
    }
}

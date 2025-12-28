package com.gdg.backend.walk.session.dto;

import com.gdg.backend.walk.session.domain.WalkSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalkSessionStatusResponse {

    private final boolean walking;
    private final Long walkSessionId;

    public static WalkSessionStatusResponse walking(WalkSession session) {
        return new WalkSessionStatusResponse(true, session.getId());
    }

    public static WalkSessionStatusResponse notWalking() {
        return new WalkSessionStatusResponse(false, null);
    }
}

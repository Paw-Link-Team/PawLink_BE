package com.gdg.backend.walk.session.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.walk.session.domain.WalkSession;
import com.gdg.backend.walk.session.dto.WalkEndRequest;
import com.gdg.backend.walk.session.dto.WalkSessionStatusResponse;
import com.gdg.backend.walk.session.dto.WalkStartResponse;
import com.gdg.backend.walk.session.service.WalkSessionService;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/walks")
@RequiredArgsConstructor
public class WalkSessionController {

    private final WalkSessionService walkSessionService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<WalkStartResponse>>start(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WalkSession session =
                walkSessionService.start(principal.userId());

        return ApiResponse.success(
                SuccessCode.OK,
                WalkStartResponse.from(session)
        );
    }

    @PostMapping("/end")
    public ResponseEntity<ApiResponse<WalkHistoryResponse>> end(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody WalkEndRequest request
    ) {
        WalkHistory history =
                walkSessionService.end(
                        principal.userId(),
                        request.getDistanceKm()
                );

        return ApiResponse.success(
                SuccessCode.OK,
                WalkHistoryResponse.from(history)
        );
    }

    @GetMapping("/session")
    public ResponseEntity<ApiResponse<WalkSessionStatusResponse>> getSession(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WalkSession session =
                walkSessionService.getCurrentSession(
                        principal.userId()
                );

        if (session == null) {
            return ApiResponse.success(
                    SuccessCode.OK,
                    new WalkSessionStatusResponse(false, null, null)
            );
        }

        return ApiResponse.success(
                SuccessCode.OK,
                WalkSessionStatusResponse.from(session)
        );
    }

}

package com.gdg.backend.walk.session.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.walk.session.dto.WalkEndRequest;
import com.gdg.backend.walk.session.dto.WalkSessionStatusResponse;
import com.gdg.backend.walk.session.dto.WalkStartResponse;
import com.gdg.backend.walk.session.service.WalkSessionService;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/walks")
@RequiredArgsConstructor
public class WalkSessionController {

    private final WalkSessionService walkSessionService;

    /* =====================
     * 산책 시작
     * ===================== */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<WalkStartResponse>> start(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.CREATED,
                WalkStartResponse.from(
                        walkSessionService.start(principal.userId())
                )
        );
    }

    /* =====================
     * 산책 종료
     * ===================== */
    @PostMapping("/{walkId}/end")
    public ResponseEntity<ApiResponse<WalkHistoryResponse>> end(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkId,
            @RequestBody @Valid WalkEndRequest request
    ) {
        return ApiResponse.success(
                SuccessCode.CREATED,
                walkSessionService.end(
                        principal.userId(),
                        walkId,
                        request.getDistanceKm(),
                        request.getMemo(),
                        request.getPoop()
                )
        );
    }

    /* =====================
     * 현재 산책 상태 조회
     * ===================== */
    @GetMapping("/session")
    public ResponseEntity<ApiResponse<WalkSessionStatusResponse>> getSession(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WalkSessionStatusResponse response =
                WalkSessionStatusResponse.from(
                        walkSessionService.getCurrentSession(
                                principal.userId()
                        )
                );

        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                response
        );
    }
}

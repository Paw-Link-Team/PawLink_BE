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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
                @AuthenticationPrincipal UserPrincipal principal// walkId를 요청 파라미터로 받음
        ) {
        return ApiResponse.success(
                SuccessCode.CREATED,
                WalkStartResponse.from(
                        walkSessionService.start(principal.userId()) // walkId를 서비스에 전달
                        )
                );
        }

    /* =====================
     * 산책 종료
     * ===================== */
    @PostMapping(value = "/{walkId}/end", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<WalkHistoryResponse>> end(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkId,
            @RequestPart("data") @Valid WalkEndRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return ApiResponse.success(
                SuccessCode.CREATED,
                walkSessionService.end(
                        principal.userId(),
                        walkId,
                        request.getDistanceKm(),
                        request.getMemo(),
                        request.getPoop(),
                        images
                )
        );
    }

    /* =====================
     * 현재 산책 상태 조회
     * ===================== */
    @GetMapping("/session")
    public WalkSessionStatusResponse getSession(@AuthenticationPrincipal UserPrincipal principal) {

        return walkSessionService.getCurrentSession(principal.userId())
                .map(WalkSessionStatusResponse::walking)
                .orElseGet(WalkSessionStatusResponse::notWalking);
    }
}

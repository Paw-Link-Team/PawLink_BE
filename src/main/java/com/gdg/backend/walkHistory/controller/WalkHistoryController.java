package com.gdg.backend.walkHistory.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.service.WalkHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "walk History api controller")
@RestController
@RequestMapping("/api/walk-histories")
@RequiredArgsConstructor
@Tag(name = "Walk History Query API")
public class WalkHistoryController {

    private final WalkHistoryService walkHistoryService;

    @Operation(
            summary = "내 산책 기록 조회",
            description = "마이페이지에서 사용하는 산책 기록 목록 조회"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<WalkHistoryResponse>>> getMyWalkHistories(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                walkHistoryService.findMyHistories(principal.userId())
        );
    }
}

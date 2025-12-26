package com.gdg.backend.walkHistory.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryDetailResponse;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.service.WalkHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Walk History API")
@RestController
@RequestMapping("/api/walk-histories")
@RequiredArgsConstructor
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
                SuccessCode.READ_SUCCESS,
                walkHistoryService.findMyHistories(principal.userId())
        );
    }

    @Operation(
            summary = "산책 기록 저장",
            description = "산책 종료 후 산책 기록을 저장합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<WalkHistoryResponse>> createWalkHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid WalkHistoryCreateRequest request
    ) {
        return ApiResponse.success(
                SuccessCode.CREATED,
                walkHistoryService.create(principal.userId(), request)
        );
    }

    @GetMapping("/{walkId}")
    public ResponseEntity<ApiResponse<WalkHistoryDetailResponse>> getDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkId
    ) {
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                walkHistoryService.getDetail(principal.userId(), walkId)
        );
    }

    @DeleteMapping("/{walkId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkId
    ) {
        walkHistoryService.delete(principal.userId(), walkId);
        return ApiResponse.success(SuccessCode.DELETE,null);
    }

}

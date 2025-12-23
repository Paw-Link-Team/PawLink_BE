package com.gdg.backend.walkHistory.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.service.WalkHistoryService;
import com.gdg.backend.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
@Tag(name = "walk History api controller")
public class WalkHistoryController {

    private final WalkHistoryService walkHistoryService;

    @Operation(summary = "히스토리 생성", description = "유저의 walk history를 생성합니다.")
    @PostMapping("/createHistory")
    public ResponseEntity<ApiResponse<WalkHistoryResponse>> createWalkHistory(
            @AuthenticationPrincipal User user,
            @RequestBody WalkHistoryCreateRequest request
    ) {

        return ApiResponse.success(SuccessCode.CREATED, walkHistoryService.create(user, request));
    }

    @Operation(summary = "히스토리 조회", description = "마이페이지에 사용할 history 정보입니다.")
    @GetMapping("/info")
    public ResponseEntity<List<WalkHistoryResponse>> getMyWalkHistories(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                walkHistoryService.findMyHistories(user)
        );
    }
}

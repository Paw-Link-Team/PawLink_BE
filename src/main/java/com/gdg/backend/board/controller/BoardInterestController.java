package com.gdg.backend.board.controller;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.dto.FavoriteBoardResponseDto;
import com.gdg.backend.board.service.BoardInterestService;
import com.gdg.backend.global.code.ErrorCode;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시글 관심 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardInterestController {

    private final BoardInterestService boardInterestService;

    @Operation(summary = "내가 관심 등록한 게시글 목록", description = "GET /boards/interests")
    @GetMapping("/interests")
    public ResponseEntity<ApiResponse<List<FavoriteBoardResponseDto>>> myInterests(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<Board> boards =
                boardInterestService.findMyInterestedBoards(principal.userId());

        List<FavoriteBoardResponseDto> result = boards.stream()
                .map(FavoriteBoardResponseDto::from)
                .toList();

        return ApiResponse.success(SuccessCode.READ_SUCCESS, result);
    }


    @Operation(summary = "관심 등록", description = "POST /boards/{boardId}/interest")
    @PostMapping("/{boardId}/interest")
    public ResponseEntity<ApiResponse<Void>> addInterest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        boardInterestService.addInterest(principal.userId(), boardId);
        return ApiResponse.success(SuccessCode.OK, null);
    }

    @Operation(summary = "관심 해제", description = "DELETE /boards/{boardId}/interest")
    @DeleteMapping("/{boardId}/interest")
    public ResponseEntity<ApiResponse<Void>> removeInterest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        boardInterestService.removeInterest(principal.userId(), boardId);
        return ApiResponse.success(SuccessCode.OK, null);
    }

    @Operation(summary = "관심 여부 확인", description = "GET /boards/{boardId}/interest")
    @GetMapping("/{boardId}/interest")
    public ResponseEntity<ApiResponse<Boolean>> isInterested(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        Long userId = principal != null ? principal.userId() : null;
        boolean result = boardInterestService.isInterested(userId, boardId);

        return ApiResponse.success(SuccessCode.READ_SUCCESS, result);
    }
}

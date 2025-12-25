package com.gdg.backend.board.controller;

import com.gdg.backend.board.dto.BoardDetailResponseDto;
import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.dto.MyBoardResponseDto;
import com.gdg.backend.board.service.BoardService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody BoardRequestDto dto
    ) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        Long boardId = boardService.create(principal.userId(), dto);
        return ApiResponse.success(SuccessCode.CREATED, boardId);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<MyBoardResponseDto>>> myBoards(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                boardService.findMyBoards(principal.userId())
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponseDto>>> findAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = getUserIdOrNull(principal);

        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                boardService.findAll(userId)
        );
    }

    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<BoardResponseDto>>> findCompleted(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = getUserIdOrNull(principal);

        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                boardService.findCompleted(userId)
        );
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDetailResponseDto>> findDetail(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.userId() : null;

        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                boardService.findDetail(boardId, userId)
        );
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId,
            @RequestBody BoardRequestDto dto
    ) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        boardService.update(boardId, principal.userId(), dto);
        return ApiResponse.success(SuccessCode.OK, null);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        boardService.delete(boardId, principal.userId());
        return ApiResponse.success(SuccessCode.OK, null);
    }

    @PostMapping("/{boardId}/complete")
    public ResponseEntity<ApiResponse<Void>> complete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        if (principal == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }

        boardService.completeBoard(boardId, principal.userId());
        return ApiResponse.success(SuccessCode.OK, null);
    }

    private Long getUserIdOrNull(UserPrincipal principal) {
        return principal != null ? principal.userId() : null;
    }
}

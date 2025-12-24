package com.gdg.backend.board.controller;

import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.service.BoardService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        Long boardId = boardService.create(principal.userId(), dto);
        return ApiResponse.success(SuccessCode.CREATED, boardId);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> findById(
            @PathVariable Long boardId
    ) {
        return ApiResponse.success(SuccessCode.READ_SUCCESS, boardService.findById(boardId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponseDto>>> findAll() {
        return ApiResponse.success(SuccessCode.READ_SUCCESS, boardService.findAll());
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId,
            @RequestBody BoardRequestDto dto
    ) {
        boardService.update(boardId, principal.userId(), dto);
        return ApiResponse.success(SuccessCode.OK, null);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        boardService.delete(boardId, principal.userId());
        return ApiResponse.success(SuccessCode.OK, null);
    }
}

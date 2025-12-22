package com.gdg.backend.board.controller;

import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.gdg.backend.global.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Long> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody BoardRequestDto dto
    ) {
        return ResponseEntity.ok(
                boardService.create(principal.userId(), dto)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> findAll() {
        return ResponseEntity.ok(boardService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody BoardRequestDto dto
    ) {
        boardService.update(id, principal.userId(), dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        boardService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}

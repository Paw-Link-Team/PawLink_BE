package com.gdg.backend.board.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    public Long create(BoardRequestDto dto) {
        Board board = Board.builder()
                .title(dto.getTitle())
                .information(dto.getInformation())
                .description(dto.getDescription())
                .build();

        return boardRepository.save(board).getId();
    }

    public BoardResponseDto findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getInformation(),
                board.getDescription()
        );
    }

    public List<BoardResponseDto> findAll() {
        return boardRepository.findAll()
                .stream()
                .map(board -> new BoardResponseDto(
                        board.getId(),
                        board.getTitle(),
                        board.getInformation(),
                        board.getDescription()
                ))
                .toList();
    }

    public void update(Long id, BoardRequestDto dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        board.setTitle(dto.getTitle());
        board.setInformation(dto.getInformation());
        board.setDescription(dto.getDescription());
    }

    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        boardRepository.delete(board);
    }
}

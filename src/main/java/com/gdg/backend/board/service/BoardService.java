package com.gdg.backend.board.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.exception.BoardNotFoundException;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(Long userId, BoardRequestDto dto) {
        User user = getUser(userId);

        Board board = Board.create(
                dto.getTitle(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getWalkTime(),
                dto.getWalkTimeType(),
                user
        );

        return boardRepository.save(board).getId();
    }

    @Transactional
    public BoardResponseDto findById(Long boardId) {
        Board board = getBoard(boardId);
        board.increaseViewCount();
        return BoardResponseDto.from(board);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> findAll() {
        return boardRepository.findAll()
                .stream()
                .map(BoardResponseDto::from)
                .toList();
    }

    @Transactional
    public void update(Long boardId, Long userId, BoardRequestDto dto) {
        Board board = getBoard(boardId);
        validateOwner(board, userId);

        board.update(
                dto.getTitle(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getWalkTime(),
                dto.getWalkTimeType()
        );
    }

    @Transactional
    public void delete(Long boardId, Long userId) {
        Board board = getBoard(boardId);
        validateOwner(board, userId);
        boardRepository.delete(board);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
    }

    private Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
    }

    private void validateOwner(Board board, Long userId) {
        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("작성자만 접근할 수 있습니다.");
        }
    }
}

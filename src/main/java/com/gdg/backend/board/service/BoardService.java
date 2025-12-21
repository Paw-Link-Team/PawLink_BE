package com.gdg.backend.board.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.exception.BoardNotFoundException;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public Long create(Long userId, BoardRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        Board board = Board.create(dto, user);
        return boardRepository.save(board).getId();
    }

    @Transactional(readOnly = true)
    public BoardResponseDto findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);

        return BoardResponseDto.from(board);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> findAll() {
        return boardRepository.findAll()
                .stream()
                .map(BoardResponseDto::from)
                .toList();
    }

    public void update(Long boardId, Long userId, BoardRequestDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        board.update(dto);
    }

    public void delete(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }
}

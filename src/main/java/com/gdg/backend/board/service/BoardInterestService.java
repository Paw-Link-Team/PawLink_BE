package com.gdg.backend.board.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.domain.BoardInterest;
import com.gdg.backend.board.exception.BoardNotFoundException;
import com.gdg.backend.board.repository.BoardInterestRepository;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardInterestService {

    private final BoardRepository boardRepository;
    private final BoardInterestRepository boardInterestRepository;
    private final UserRepository userRepository;

    public void addInterest(Long userId, Long boardId) {
        if (boardInterestRepository.existsByUserIdAndBoardId(userId, boardId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        boardInterestRepository.save(BoardInterest.create(user, board));
    }

    public void removeInterest(Long userId, Long boardId) {
        boardInterestRepository.deleteByUserIdAndBoardId(userId, boardId);
    }

    @Transactional(readOnly = true)
    public boolean isInterested(Long userId, Long boardId) {
        if (userId == null) return false;
        return boardInterestRepository.existsByUserIdAndBoardId(userId, boardId);
    }

    @Transactional(readOnly = true)
    public long countInterest(Long boardId) {
        return boardInterestRepository.countByBoardId(boardId);
    }

    @Transactional(readOnly = true)
    public List<Board> findMyInterestedBoards(Long userId) {
        return boardInterestRepository.findAllByUserId(userId)
                .stream()
                .map(BoardInterest::getBoard)
                .toList();
    }
}

package com.gdg.backend.board.repository;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.domain.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByStatusOrderByIdDesc(BoardStatus status);
    List<Board> findByUserIdOrderByIdDesc(Long userId);

}

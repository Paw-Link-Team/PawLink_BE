package com.gdg.backend.board.repository;

import com.gdg.backend.board.domain.BoardInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardInterestRepository extends JpaRepository<BoardInterest, Long> {

    Optional<BoardInterest> findByUserIdAndBoardId(Long userId, Long boardId);

    boolean existsByUserIdAndBoardId(Long userId, Long boardId);

    void deleteByUserIdAndBoardId(Long userId, Long boardId);

    long countByBoardId(Long boardId);

    List<BoardInterest> findAllByUserId(Long userId);
}

package com.gdg.backend.board.repository;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.domain.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByStatusOrderByIdDesc(BoardStatus status);
    List<Board> findByUserIdOrderByIdDesc(Long userId);

    @Modifying
    @Query("delete from Board b where b.pet.id = :petId")
    void deleteAllByPetId(@Param("petId") Long petId);
    @Query("""
        select b from Board b
        where lower(b.title) like lower(concat('%', :keyword, '%'))
           or lower(b.description) like lower(concat('%', :keyword, '%'))
        order by b.id desc
    """)
    List<Board> searchByKeyword(String keyword);

}

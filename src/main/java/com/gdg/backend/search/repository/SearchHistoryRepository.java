package com.gdg.backend.search.repository;

import com.gdg.backend.search.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SearchHistory> findByUserIdAndKeyword(Long userId, String keyword);

    void deleteByUserId(Long userId);

    long countByUserId(Long userId);

    List<SearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}

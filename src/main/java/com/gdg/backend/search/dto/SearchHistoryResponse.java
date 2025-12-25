package com.gdg.backend.search.dto;

import com.gdg.backend.search.domain.SearchHistory;

import java.time.LocalDateTime;

public record SearchHistoryResponse(
        Long id,
        String keyword,
        LocalDateTime searchedAt
) {
    public static SearchHistoryResponse from(SearchHistory h) {
        return new SearchHistoryResponse(h.getId(), h.getKeyword(), h.getCreatedAt());
    }
}

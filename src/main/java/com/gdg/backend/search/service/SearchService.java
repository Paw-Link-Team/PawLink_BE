package com.gdg.backend.search.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.search.domain.SearchHistory;
import com.gdg.backend.search.dto.BoardSearchItem;
import com.gdg.backend.search.dto.SearchHistoryResponse;
import com.gdg.backend.search.dto.SearchResponse;
import com.gdg.backend.search.repository.SearchHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int HISTORY_LIMIT = 10;

    private final SearchHistoryRepository historyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public SearchResponse search(Long userId, String keywordRaw) {
        String keyword = normalize(keywordRaw);

        upsertHistory(userId, keyword);
        trimHistoryIfNeeded(userId);

        List<Board> boards = boardRepository.searchByKeyword(keyword);

        List<BoardSearchItem> items = boards.stream()
                .limit(30) // 결과 과다 방지 (프론트 처음 연결용)
                .map(b -> new BoardSearchItem(
                        b.getId(),
                        b.getTitle(),
                        b.getLocation(),
                        b.getWalkTimeType().name()
                ))
                .toList();

        return new SearchResponse(keyword, items);
    }

    @Transactional(readOnly = true)
    public List<SearchHistoryResponse> getHistory(Long userId) {
        return historyRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SearchHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void deleteHistoryItem(Long userId, Long historyId) {
        SearchHistory h = historyRepository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException("검색 기록이 없습니다."));

        if (!h.getUserId().equals(userId)) {
            // 너희 프로젝트 예외로 교체 가능
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        historyRepository.delete(h);
    }

    @Transactional
    public void clearHistory(Long userId) {
        historyRepository.deleteByUserId(userId);
    }

    private void upsertHistory(Long userId, String keyword) {
        historyRepository.findByUserIdAndKeyword(userId, keyword)
                .ifPresentOrElse(existing -> {
                    existing.touchNow();
                    // dirty checking
                }, () -> historyRepository.save(SearchHistory.of(userId, keyword)));
    }

    private void trimHistoryIfNeeded(Long userId) {
        long count = historyRepository.countByUserId(userId);
        if (count <= HISTORY_LIMIT) return;

        // 오래된 것부터 삭제: desc로 가져온 뒤 뒤쪽이 오래된 것
        List<SearchHistory> allDesc = historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<SearchHistory> toDelete = allDesc.subList(HISTORY_LIMIT, allDesc.size());
        historyRepository.deleteAllInBatch(toDelete);
    }

    private String normalize(String keyword) {
        if (keyword == null) throw new IllegalArgumentException("keyword는 필수입니다.");
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("keyword는 비어있을 수 없습니다.");
        if (trimmed.length() > 50) throw new IllegalArgumentException("keyword는 50자 이하여야 합니다.");
        return trimmed;
    }
}

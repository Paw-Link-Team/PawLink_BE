package com.gdg.backend.walkHistory.service;

import com.gdg.backend.user.domain.User;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.repository.WalkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkHistoryService {

    private final WalkHistoryRepository walkHistoryRepository;

    @Transactional
    public WalkHistoryResponse create(User user, WalkHistoryCreateRequest request) {

        validateRequest(request);

        WalkHistory walkHistory = WalkHistory.builder()
                .user(user)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .distanceKm(request.getDistanceKm())
                .build();

        walkHistoryRepository.save(walkHistory);

        return WalkHistoryResponse.from(walkHistory);
    }

    @Transactional(readOnly = true)
    public List<WalkHistoryResponse> findMyHistories(User user) {

        return walkHistoryRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(WalkHistoryResponse::from)
                .toList();
    }


    private void validateRequest(WalkHistoryCreateRequest request) {

        if (request.getStartedAt() == null || request.getEndedAt() == null) {
            throw new IllegalArgumentException("산책 시작/종료 시각은 필수입니다.");
        }

        if (request.getEndedAt().isBefore(request.getStartedAt())) {
            throw new IllegalArgumentException("종료 시각은 시작 시각 이후여야 합니다.");
        }

        if (request.getDistanceKm() == null || request.getDistanceKm().signum() <= 0) {
            throw new IllegalArgumentException("이동 거리는 0보다 커야 합니다.");
        }
    }
}

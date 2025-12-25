package com.gdg.backend.walkHistory.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.repository.WalkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkHistoryService {

    private final WalkHistoryRepository walkHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public WalkHistory save(
            User user,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            BigDecimal distanceKm
    ) {

        validate(startedAt, endedAt, distanceKm);

        WalkHistory history = WalkHistory.builder()
                .user(user)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .distanceKm(distanceKm)
                .build();

        return walkHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<WalkHistoryResponse> findMyHistories(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        return walkHistoryRepository
                .findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(WalkHistoryResponse::from)
                .toList();
    }

    private void validate(
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            BigDecimal distanceKm
    ) {
        if (startedAt == null || endedAt == null) {
            throw new IllegalArgumentException("산책 시작/종료 시각은 필수입니다.");
        }
        if (endedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("종료 시각은 시작 시각 이후여야 합니다.");
        }
        if (distanceKm == null || distanceKm.signum() <= 0) {
            throw new IllegalArgumentException("이동 거리는 0보다 커야 합니다.");
        }
    }
}

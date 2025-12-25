package com.gdg.backend.walker.walkHistory.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walker.walkHistory.domain.WalkHistory;
import com.gdg.backend.walker.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walker.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walker.walkHistory.repository.WalkHistoryRepository;
import com.gdg.backend.walker.domain.WalkerProfile;
import com.gdg.backend.walker.repository.WalkerProfileRepository;
import com.gdg.backend.wallet.service.WalletService;
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
    private final WalkerProfileRepository walkerProfileRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    @Transactional
    public WalkHistoryResponse create(Long userId, WalkHistoryCreateRequest request) {

        validateRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        WalkHistory walkHistory = WalkHistory.builder()
                .user(user)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .distanceKm(request.getDistanceKm())
                .build();

        walkHistoryRepository.save(walkHistory);

        walletService.earn(userId, 100, "산책 완료");

        return WalkHistoryResponse.from(walkHistory);
    }

    @Transactional(readOnly = true)
    public List<WalkHistoryResponse> findMyHistories(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        return walkHistoryRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(WalkHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void saveWalkHistory(
            User user,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            BigDecimal distanceKm
    ) {
        WalkHistory history = WalkHistory.builder()
                .user(user)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .distanceKm(distanceKm)
                .build();

        walkHistoryRepository.save(history);

        WalkerProfile profile =
                walkerProfileRepository.findByUser(user)
                        .orElseThrow(() -> new IllegalStateException("WalkerProfile 없음"));

        profile.addWalk(distanceKm);
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

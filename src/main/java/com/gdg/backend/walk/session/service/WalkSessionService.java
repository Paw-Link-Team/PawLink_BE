package com.gdg.backend.walk.session.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walk.session.domain.WalkSession;
import com.gdg.backend.walk.session.repository.WalkSessionRepository;
import com.gdg.backend.walkHistory.domain.PoopStatus;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.service.WalkHistoryService;
import com.gdg.backend.walker.walkerProfile.service.WalkerProfileService;
import com.gdg.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalkSessionService {

    private final WalkSessionRepository walkSessionRepository;
    private final WalkHistoryService walkHistoryService;
    private final WalkerProfileService walkerProfileService;
    private final WalletService walletService;
    private final UserRepository userRepository;

    @Transactional
    public WalkSession start(Long userId) {
        User user = existUser(userId);

        if (walkSessionRepository.existsByUser(user)) {
            throw new IllegalStateException("이미 진행 중인 산책이 있습니다.");
        }

        return walkSessionRepository.save(WalkSession.start(user));
    }

    @Transactional
    public WalkHistoryResponse end(
            Long userId,
            Long walkId,
            BigDecimal distanceKm,
            String memo,
            PoopStatus poop
    ) {
        User user = existUser(userId);
        validateDistance(distanceKm);

        WalkSession session = walkSessionRepository.findById(walkId)
                .orElseThrow(() ->
                        new IllegalStateException("해당 산책 세션이 존재하지 않습니다.")
                );

        if (!session.getUser().getId().equals(userId)) {
            throw new SecurityException("산책 종료 권한이 없습니다.");
        }

        LocalDateTime endedAt = LocalDateTime.now();

        WalkHistoryCreateRequest request =
                WalkHistoryCreateRequest.builder()
                        .startedAt(session.getStartedAt())
                        .endedAt(endedAt)
                        .distanceKm(distanceKm)
                        .memo(memo)
                        .poop(poop)
                        .build();

        WalkHistoryResponse history =
                walkHistoryService.create(userId, request);

        // 후처리
        walkerProfileService.addWalk(user, distanceKm);
        walletService.earn(userId, 100, "산책 완료");

        walkSessionRepository.delete(session);

        return history;
    }

    @Transactional(readOnly = true)
    public boolean isWalking(Long userId) {
        return walkSessionRepository.existsByUser(existUser(userId));
    }

    @Transactional(readOnly = true)
    public WalkSession getCurrentSession(Long userId) {
        return walkSessionRepository.findByUser(existUser(userId)).orElse(null);
    }

    /* =====================
     * 내부 유틸
     * ===================== */
    private void validateDistance(BigDecimal distanceKm) {
        if (distanceKm == null || distanceKm.signum() <= 0) {
            throw new IllegalArgumentException("이동 거리는 0보다 커야 합니다.");
        }
    }

    private User existUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("유저를 찾을 수 없습니다."));
    }
}

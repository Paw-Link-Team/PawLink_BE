package com.gdg.backend.walk.session.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walk.session.domain.WalkSession;
import com.gdg.backend.walk.session.repository.WalkSessionRepository;
import com.gdg.backend.walkHistory.domain.WalkHistory;
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

        WalkSession session = WalkSession.start(user);
        return walkSessionRepository.save(session);
    }

    @Transactional
    public WalkHistory end(Long userId, BigDecimal distanceKm) {

        User user = existUser(userId);

        validateDistance(distanceKm);

        WalkSession session = walkSessionRepository.findByUser(user)
                .orElseThrow(() ->
                        new IllegalStateException("진행 중인 산책이 없습니다.")
                );

        WalkHistory history =
                walkHistoryService.save(
                        user,
                        session.getStartedAt(),
                        LocalDateTime.now(),
                        distanceKm
                );

        walkerProfileService.addWalk(user, distanceKm);
        walletService.earn(user.getId(), 100, "산책 완료");

        walkSessionRepository.delete(session);
        return history;
    }

    @Transactional(readOnly = true)
    public boolean isWalking(Long userId) {
        User user = existUser(userId);
        return walkSessionRepository.existsByUser(user);
    }

    @Transactional(readOnly = true)
    public WalkSession getCurrentSession(Long userId) {
        User user = existUser(userId);
        return walkSessionRepository.findByUser(user).orElse(null);
    }

    private void validateDistance(BigDecimal distanceKm) {
        if (distanceKm == null || distanceKm.signum() <= 0) {
            throw new IllegalArgumentException("이동 거리는 0보다 커야 합니다.");
        }
    }

    private User existUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
    }
}

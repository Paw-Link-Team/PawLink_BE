package com.gdg.backend.walk.session.service;

import com.gdg.backend.chat.service.ChatService;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.util.S3Uploader;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walk.session.domain.WalkSession;
import com.gdg.backend.walk.session.repository.WalkSessionRepository;
import com.gdg.backend.walkHistory.domain.PoopStatus;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import com.gdg.backend.walkHistory.domain.WalkHistoryImage;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.repository.WalkHistoryImageRepository;
import com.gdg.backend.walkHistory.service.WalkHistoryService;
import com.gdg.backend.walker.walkerProfile.service.WalkerProfileService;
import com.gdg.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkSessionService {

    private final WalkSessionRepository walkSessionRepository;
    private final WalkHistoryService walkHistoryService;
    private final WalkHistoryImageRepository walkHistoryImageRepository;
    private final S3Uploader s3Uploader;
    private final WalkerProfileService walkerProfileService;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final ChatService chatService;

    @Transactional
public WalkSession start(Long userId, Long walkId) {
    // 사용자 존재 확인
    User user = existUser(userId);

    // 이미 진행 중인 산책이 있는지 확인
    if (walkSessionRepository.existsByUser(user)) {
        throw new IllegalStateException("이미 진행 중인 산책이 있습니다.");
    }

    // walkId를 사용하여 새로운 WalkSession 시작
    WalkSession walkSession = WalkSession.start(user, walkId);

    // WalkSession 저장
    return walkSessionRepository.save(walkSession);
}

    @Transactional
    public WalkHistoryResponse end(
            Long userId,
            Long walkId,
            BigDecimal distanceKm,
            String memo,
            PoopStatus poop,
            List<MultipartFile> images
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

        // ✅ 산책 사진 저장
        saveImages(history.getId(), images);

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

    private User existUser(Long userId, Long walkId) {
    // 주어진 userId로 사용자가 존재하는지 확인
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

    // walkId에 대한 검증을 추가, 예를 들어 이미 해당 walkId로 진행 중인 세션이 있는지 확인
    WalkSession existingSession = walkSessionRepository.findByWalkId(walkId);
    if (existingSession != null && !existingSession.getUser().equals(user)) {
        throw new IllegalStateException("이미 해당 walkId로 진행 중인 산책 세션이 있습니다.");
    }

    return user;
}

}

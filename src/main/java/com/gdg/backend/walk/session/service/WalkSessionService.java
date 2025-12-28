package com.gdg.backend.walk.session.service;

import com.gdg.backend.chat.service.ChatService;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.util.S3Uploader;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walk.session.domain.WalkSession;
import com.gdg.backend.walk.session.repository.WalkSessionRepository;
import com.gdg.backend.walkHistory.domain.PoopStatus;
import com.gdg.backend.walkHistory.domain.WalkHistoryImage;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.repository.WalkHistoryImageRepository;
import com.gdg.backend.walkHistory.service.WalkHistoryService;
import com.gdg.backend.walker.walkerProfile.service.WalkerProfileService;
import com.gdg.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    /* =====================
     * 산책 시작
     * ===================== */
    @Transactional
    public WalkSession start(Long userId) {

        // 이미 있으면 그대로 반환
        return walkSessionRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    try {
                        WalkSession session = WalkSession.start(userId);
                        return walkSessionRepository.save(session);
                    } catch (DataIntegrityViolationException e) {
                        // 동시 요청으로 다른 트랜잭션이 먼저 만든 경우
                        return walkSessionRepository
                                .findByUserId(userId)
                                .orElseThrow();
                    }
                });
    }


    /* =====================
     * 산책 종료
     * ===================== */
    @Transactional
    public WalkHistoryResponse end(
            Long userId,
            Long walkSessionId,
            BigDecimal distanceKm,
            String memo,
            PoopStatus poop,
            List<MultipartFile> images
    ) {
        validateDistance(distanceKm);

        WalkSession session = walkSessionRepository.findById(walkSessionId)
                .orElseThrow(() ->
                        new IllegalStateException("해당 산책 세션이 존재하지 않습니다.")
                );

        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("산책 종료 권한이 없습니다.");
        }

        User user = existUser(userId); // 후처리용


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

        // 산책 사진 저장
        saveImages(history.getId(), images);

        // 후처리
        walkerProfileService.addWalk(user, distanceKm);
        walletService.earn(userId, 100, "산책 완료");

        // 세션 종료
        walkSessionRepository.delete(session);

        return history;
    }

    /* =====================
     * 상태 조회
     * ===================== */
    @Transactional(readOnly = true)
    public boolean isWalking(Long userId) {
        return walkSessionRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<WalkSession> getCurrentSession(Long userId) {
        return walkSessionRepository.findByUserId(userId);
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

    private void saveImages(Long historyId, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return;

        for (MultipartFile image : images) {
            String imageUrl = s3Uploader.upload(image, "walk-history");

            WalkHistoryImage historyImage = WalkHistoryImage.of(historyId, imageUrl);

            walkHistoryImageRepository.save(historyImage);
        }
    }
}

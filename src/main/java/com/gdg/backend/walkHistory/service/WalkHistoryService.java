package com.gdg.backend.walkHistory.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.util.S3Uploader;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walkHistory.domain.WalkHistory;
import com.gdg.backend.walkHistory.domain.WalkHistoryImage;
import com.gdg.backend.walkHistory.dto.WalkHistoryCreateRequest;
import com.gdg.backend.walkHistory.dto.WalkHistoryDetailResponse;
import com.gdg.backend.walkHistory.dto.WalkHistoryResponse;
import com.gdg.backend.walkHistory.repository.WalkHistoryImageRepository;
import com.gdg.backend.walkHistory.repository.WalkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WalkHistoryService {

    private final WalkHistoryRepository walkHistoryRepository;
    private final WalkHistoryImageRepository walkHistoryImageRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public WalkHistoryResponse create(
            Long userId,
            WalkHistoryCreateRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        validate(request);

        WalkHistory history = WalkHistory.builder()
                .user(user)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .distanceKm(request.getDistanceKm())
                .memo(request.getMemo())
                .poop(request.getPoop())
                .build();

        walkHistoryRepository.save(history);

        return WalkHistoryResponse.from(history);
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

    @Transactional(readOnly = true)
    public WalkHistoryDetailResponse getDetail(Long userId, Long walkId) {
        WalkHistory history = walkHistoryRepository.findById(walkId)
                .orElseThrow(() -> new NoSuchElementException("산책 기록 없음"));

        if (!history.getUser().getId().equals(userId)) {
            throw new SecurityException("조회 권한 없음");
        }

        List<String> images =
                walkHistoryImageRepository.findByWalkHistoryId(walkId).stream()
                        .map(WalkHistoryImage::getImageUrl)
                        .toList();

        return WalkHistoryDetailResponse.of(history, images);
    }

    @Transactional
    public void delete(Long userId, Long walkId) {
        WalkHistory history = walkHistoryRepository.findById(walkId)
                .orElseThrow(() -> new NoSuchElementException("산책 기록 없음"));

        if (!history.getUser().getId().equals(userId)) {
            throw new SecurityException("삭제 권한 없음");
        }

        // 1️⃣ 이미지 URL 조회
        List<WalkHistoryImage> images =
                walkHistoryImageRepository.findByWalkHistoryId(walkId);

        // 2️⃣ S3 삭제
        for (WalkHistoryImage image : images) {
            s3Uploader.delete(image.getImageUrl());
        }

        // 3️⃣ DB 정리
        walkHistoryImageRepository.deleteByWalkHistoryId(walkId);
        walkHistoryRepository.delete(history);
    }



    private void validate(WalkHistoryCreateRequest request) {
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

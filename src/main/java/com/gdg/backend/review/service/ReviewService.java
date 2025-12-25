package com.gdg.backend.review.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.review.domain.Review;
import com.gdg.backend.review.dto.ReviewCreateRequest;
import com.gdg.backend.review.dto.ReviewUpdateRequest;
import com.gdg.backend.review.repository.ReviewRepository;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import com.gdg.backend.walker.domain.WalkerProfile;
import com.gdg.backend.walker.repository.WalkerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final WalkerProfileRepository walkerProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createReview(
            Long writerUserId,
            Long walkerUserId,
            ReviewCreateRequest request
    ) {
        User writer = userRepository.findById(writerUserId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        WalkerProfile walkerProfile =
                walkerProfileRepository.findByUserId(walkerUserId)
                        .orElseThrow(() -> new UserNotFoundException("산책가가 없습니다."));

        Review review = Review.builder()
                .walkerProfile(walkerProfile)
                .writer(writer)
                .rating(request.rating())
                .content(request.content())
                .build();

        reviewRepository.save(review);

        walkerProfile.addReview(request.rating());
    }

    @Transactional
    public void updateReview(
            Long reviewId,
            Long userId,
            ReviewUpdateRequest request
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 없음"));

        if (!review.getWriter().getId().equals(userId)) {
            throw new SecurityException("수정 권한 없음");
        }

        int oldRating = review.getRating();

        review.update(request.rating(), request.content());

        review.getWalkerProfile()
                .updateReview(oldRating, request.rating());
    }

    @Transactional
    public void deleteReview(
            Long reviewId,
            Long userId
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 없음"));

        if (!review.getWriter().getId().equals(userId)) {
            throw new SecurityException("삭제 권한 없음");
        }

        WalkerProfile profile = review.getWalkerProfile();

        profile.removeReview(review.getRating());

        reviewRepository.delete(review);
    }

}

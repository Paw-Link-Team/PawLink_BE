package com.gdg.backend.review.service;

import com.gdg.backend.review.dto.ReviewResponse;
import com.gdg.backend.review.repository.ReviewRepository;
import com.gdg.backend.walker.domain.WalkerProfile;
import com.gdg.backend.walker.repository.WalkerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final WalkerProfileRepository walkerProfileRepository;

    public List<ReviewResponse> getReviews(Long walkerUserId) {
        WalkerProfile walkerProfile =
                walkerProfileRepository.findByUserId(walkerUserId)
                        .orElseThrow();

        return reviewRepository
                .findByWalkerProfileOrderByCreatedAtDesc(walkerProfile)
                .stream()
                .map(r -> new ReviewResponse(
                        r.getWriter().getNickname(),
                        r.getWriter().getProfileImageUrl(),
                        r.getRating(),
                        r.getContent(),
                        r.getCreatedAt()
                ))
                .toList();
    }
}

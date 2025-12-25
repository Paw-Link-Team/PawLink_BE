package com.gdg.backend.review.controller;

import com.gdg.backend.review.dto.ReviewCreateRequest;
import com.gdg.backend.review.dto.ReviewResponse;
import com.gdg.backend.review.dto.ReviewUpdateRequest;
import com.gdg.backend.review.service.ReviewQueryService;
import com.gdg.backend.review.service.ReviewService;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/walkers/{walkerUserId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping
    public void createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkerUserId,
            @RequestBody ReviewCreateRequest request
    ) {
        reviewService.createReview(
                principal.userId(),
                walkerUserId,
                request
        );
    }

    @PatchMapping("/{reviewId}")
    public void updateReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkerUserId,
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateRequest request
    ) {
        reviewService.updateReview(
                reviewId,
                principal.userId(),
                request
        );
    }

    @GetMapping
    public List<ReviewResponse> getReviews(
            @PathVariable Long walkerUserId
    ) {
        return reviewQueryService.getReviews(walkerUserId);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long walkerUserId,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(
                reviewId,
                principal.userId()
        );
    }

}

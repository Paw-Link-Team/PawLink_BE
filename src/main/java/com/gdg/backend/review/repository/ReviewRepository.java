package com.gdg.backend.review.repository;

import com.gdg.backend.review.domain.Review;
import com.gdg.backend.walker.domain.WalkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByWalkerProfileOrderByCreatedAtDesc(
            WalkerProfile walkerProfile
    );
}

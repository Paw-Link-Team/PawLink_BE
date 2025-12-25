package com.gdg.backend.walker.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "walker_profile",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal totalDistanceKm;

    @Column(nullable = false)
    private int walkCount;

    @Column(nullable = false)
    private double avgRating;

    @Column(nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private int careerYears;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int ratingSum;

    @Builder
    private WalkerProfile(User user) {
        this.user = user;
        this.totalDistanceKm = BigDecimal.ZERO;
        this.walkCount = 0;
        this.avgRating = 0.0;
        this.reviewCount = 0;
        this.careerYears = 1;
        this.updatedAt = LocalDateTime.now();
    }

    public void addWalk(BigDecimal distanceKm) {
        this.totalDistanceKm = this.totalDistanceKm.add(distanceKm);
        this.walkCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void addReview(int rating) {
        this.ratingSum += rating;
        this.reviewCount++;
        this.avgRating = (double) ratingSum / reviewCount;
    }

    public void updateReview(int oldRating, int newRating) {
        this.ratingSum = this.ratingSum - oldRating + newRating;
        this.avgRating = (double) ratingSum / reviewCount;
    }

    public void removeReview(int rating) {
        this.ratingSum -= rating;
        this.reviewCount--;

        if (this.reviewCount == 0) {
            this.avgRating = 0.0;
            this.ratingSum = 0;
        } else {
            this.avgRating = (double) ratingSum / reviewCount;
        }
    }

}

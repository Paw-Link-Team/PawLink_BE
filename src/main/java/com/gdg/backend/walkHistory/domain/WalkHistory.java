package com.gdg.backend.walkHistory.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "walk_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 산책 시작 시각
    @Column(nullable = false)
    private LocalDateTime startedAt;

    // 산책 종료 시각
    @Column(nullable = false)
    private LocalDateTime endedAt;

    // 이동 거리 (km)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal distanceKm;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private WalkHistory(
            User user,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            BigDecimal distanceKm
    ) {
        this.user = user;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.distanceKm = distanceKm;
        this.createdAt = LocalDateTime.now();
    }

    /* ===== 파생값 ===== */

    public Duration getDuration() {
        return Duration.between(startedAt, endedAt);
    }

    public double getAverageSpeedKmPerHour() {
        double hours = getDuration().toSeconds() / 3600.0;
        return hours == 0 ? 0 : distanceKm.doubleValue() / hours;
    }
}


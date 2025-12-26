package com.gdg.backend.walkHistory.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    // 산책 시작 / 종료
    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    // 총 산책 시간 (초)
    @Column(nullable = false)
    private int durationSec;

    // 이동 거리 (km)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal distanceKm;

    // 평균 속도
    @Column(nullable = false)
    private double avgSpeed;

    // 산책 메모
    @Column(length = 500)
    private String memo;

    // 배변 여부
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PoopStatus poop;

    @Builder
    private WalkHistory(
            User user,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            BigDecimal distanceKm,
            String memo,
            PoopStatus poop
    ) {
        this.user = user;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.distanceKm = distanceKm;
        this.memo = memo;
        this.poop = poop;

        this.durationSec =
                (int) Duration.between(startedAt, endedAt).getSeconds();

        double hours = durationSec / 3600.0;
        this.avgSpeed =
                hours == 0 ? 0 : distanceKm.doubleValue() / hours;
    }
}

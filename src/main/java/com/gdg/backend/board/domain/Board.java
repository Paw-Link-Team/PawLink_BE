package com.gdg.backend.board.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String location;

    @Column
    private LocalDateTime walkTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WalkTimeType walkTimeType;

    @Column(nullable = false)
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private Board(
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            User user
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.walkTime = walkTime;
        this.walkTimeType = walkTimeType;
        this.user = user;
        this.viewCount = 0L;
    }

    public static Board create(
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            User user
    ) {
        return Board.builder()
                .title(title)
                .description(description)
                .location(location)
                .walkTime(walkTime)
                .walkTimeType(walkTimeType)
                .user(user)
                .build();
    }

    public void update(
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.walkTime = walkTime;
        this.walkTimeType = walkTimeType;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}

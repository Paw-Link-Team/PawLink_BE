package com.gdg.backend.board.domain;

import com.gdg.backend.pet.domain.Pet;
import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Builder
    private Board(
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            User user,
            Pet pet
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.walkTime = walkTime;
        this.walkTimeType = walkTimeType;
        this.user = user;
        this.pet = pet;
        this.viewCount = 0L;
        this.status = BoardStatus.OPEN;
    }
    public static Board create(
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            User user,
            Pet pet
    ) {
        return Board.builder()
                .title(title)
                .description(description)
                .location(location)
                .walkTime(walkTime)
                .walkTimeType(walkTimeType)
                .user(user)
                .pet(pet)
                .build();
    }


    public void update(
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            Pet pet
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.walkTimeType = walkTimeType;

        // ⭐ 시간 유형에 따른 분기 (중요)
        if (walkTimeType == WalkTimeType.UNDECIDED) {
            this.walkTime = null;
        } else {
            this.walkTime = walkTime;
        }

        // ⭐ pet 변경 허용 (null이면 그대로 두고 싶으면 분기 가능)
        if (pet != null) {
            this.pet = pet;
        }
    }



    public void increaseViewCount() {
        this.viewCount++;
    }

    public void complete() {
        this.status = BoardStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.status == BoardStatus.COMPLETED;
    }

    public void changePet(Pet pet) {
        this.pet = pet;
    }

}

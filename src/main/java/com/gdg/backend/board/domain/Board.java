package com.gdg.backend.board.domain;

import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String information;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 조회수
    private Long viewCount = 0L;

    // 조회수 증가 메서드
    public void increaseViewCount() {
        this.viewCount++;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private Board(String title, String information, String description, User user) {
        this.title = title;
        this.information = information;
        this.description = description;
        this.user = user;
    }

    public static Board create(BoardRequestDto dto, User user) {
        return Board.builder()
                .title(dto.getTitle())
                .information(dto.getInformation())
                .description(dto.getDescription())
                .user(user)
                .build();
    }

    public void update(BoardRequestDto dto) {
        this.title = dto.getTitle();
        this.information = dto.getInformation();
        this.description = dto.getDescription();
    }
}

package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.domain.WalkTimeType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private final Long id;
    private final String title;
    private final String description;
    private final String location;
    private final LocalDateTime walkTime;
    private final WalkTimeType walkTimeType;
    private final Long viewCount;
    private final Long userId;
    private final String userNickname;

    private BoardResponseDto(
            Long id,
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            Long viewCount,
            Long userId,
            String userNickname
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.walkTime = walkTime;
        this.walkTimeType = walkTimeType;
        this.viewCount = viewCount;
        this.userId = userId;
        this.userNickname = userNickname;
    }

    public static BoardResponseDto from(Board board) {
        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getLocation(),
                board.getWalkTime(),
                board.getWalkTimeType(),
                board.getViewCount(),
                board.getUser().getId(),
                board.getUser().getNickname()
        );
    }
}

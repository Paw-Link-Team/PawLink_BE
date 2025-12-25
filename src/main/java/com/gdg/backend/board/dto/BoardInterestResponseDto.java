package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.Board;
import lombok.Getter;

@Getter
public class BoardInterestResponseDto {

    private final Long boardId;
    private final String title;
    private final String description;
    private final String location;
    private final Long viewCount;

    private BoardInterestResponseDto(
            Long boardId,
            String title,
            String description,
            String location,
            Long viewCount
    ) {
        this.boardId = boardId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.viewCount = viewCount;
    }

    public static BoardInterestResponseDto from(Board board) {
        return new BoardInterestResponseDto(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getLocation(),
                board.getViewCount()
        );
    }
}

package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteBoardResponseDto {

    private Long boardId;
    private String title;
    private String description;
    private String location;
    private Long viewCount;

    public static FavoriteBoardResponseDto from(Board board) {
        return new FavoriteBoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getLocation(),
                board.getViewCount()
        );
    }
}

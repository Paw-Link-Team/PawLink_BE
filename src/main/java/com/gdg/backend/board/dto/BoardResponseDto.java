package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String title;
    private String information;
    private String description;
    private Long viewCount;
    private Long userId;
    private String userNickname;

    public static BoardResponseDto from(Board board) {
        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getInformation(),
                board.getDescription(),
                board.getViewCount(),
                board.getUser().getId(),
                board.getUser().getNickname()
        );
    }
}

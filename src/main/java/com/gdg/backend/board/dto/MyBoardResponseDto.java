package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyBoardResponseDto {

    private Long boardId;
    private String title;
    private String description;
    private String status; // OPEN / COMPLETED

    public static MyBoardResponseDto from(Board board) {
        return new MyBoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getStatus().name()
        );
    }
}


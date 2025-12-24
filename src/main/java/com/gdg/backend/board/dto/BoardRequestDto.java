package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.WalkTimeType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardRequestDto {
    private String title;
    private String description;
    private LocalDateTime walkTime;
    private WalkTimeType walkTimeType;
    private String location;

}

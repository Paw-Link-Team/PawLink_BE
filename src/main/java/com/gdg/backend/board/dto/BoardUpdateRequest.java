package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.WalkTimeType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private LocalDateTime walkTime;

    private WalkTimeType walkTimeType;

    @NotBlank
    private String location;

    private Long petId;
}

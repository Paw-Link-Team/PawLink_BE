package com.gdg.backend.board.dto;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.domain.BoardStatus;
import com.gdg.backend.board.domain.WalkTimeType;
import com.gdg.backend.pet.dto.PetProfileDto;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardDetailResponseDto {

    private final Long id;
    private final String title;
    private final String description;
    private final String location;

    private final LocalDateTime walkTime;
    private final WalkTimeType walkTimeType;

    private final Long viewCount;

    private final Long userId;
    private final String userNickname;
    private final String userProfileImageUrl;

    private boolean interested;
    private long interestCount;

    private BoardStatus status;

    private PetProfileDto petProfileDto;

    private boolean myBoard;



    private BoardDetailResponseDto(
            Long id,
            String title,
            String description,
            String location,
            LocalDateTime walkTime,
            WalkTimeType walkTimeType,
            Long viewCount,
            Long userId,
            String userNickname,
            String userProfileImageUrl,
            BoardStatus status
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
        this.userProfileImageUrl = userProfileImageUrl;
        this.status = status;
    }

    public static BoardDetailResponseDto from(Board board) {
        return new BoardDetailResponseDto(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getLocation(),
                board.getWalkTime(),
                board.getWalkTimeType(),
                board.getViewCount(),
                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getUser().getProfileImageUrl(),
                board.getStatus()
        );
    }

    public BoardDetailResponseDto applyInterest(boolean interested, long interestCount) {
        this.interested = interested;
        this.interestCount = interestCount;
        return this;
    }

    public BoardDetailResponseDto withMyBoard(boolean myBoard) {
        this.myBoard = myBoard;
        return this;
    }


    public BoardDetailResponseDto withDogProfile(PetProfileDto petProfileDto) {
        this.petProfileDto = petProfileDto;
        return this;
    }

}

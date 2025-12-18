package com.gdg.backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    //200
    OK(0, HttpStatus.OK, "요청이 정상적으로 처리되었습니다."),
    READ_SUCCESS(1, HttpStatus.OK, "조회가 완료되었습니다."),
    LOGIN_SUCCESS(2, HttpStatus.OK, "로그인이 완료되었습니다."),
    LOGOUT_SUCCESS(3, HttpStatus.OK, "로그아웃이 완료되었습니다."),
    PROCESS_SUCCESS(4, HttpStatus.OK, "정상적으로 처리되었습니다."),

    //201
    CREATED(100, HttpStatus.CREATED, "게시판 작성 요청이 정상적으로 처리되었습니다."),
    USER_CREATED(101, HttpStatus.CREATED, "사용자가 정상적으로 생성되었습니다."),
    PET_CREATED(102, HttpStatus.CREATED, "반려동물이 정상적으로 생성되었습니다."),
    ADMIN_CREATED(103, HttpStatus.CREATED, "관리자 계정이 생성되었습니다."),

    //202
    UPDATE(200, HttpStatus.OK, "게시판 수정이 정상적으로 처리되었습니다."),
    USER_UPDATE(201, HttpStatus.OK, "사용자 수정이 정상적으로 처리되었습니다."),
    PET_UPDATE(202, HttpStatus.OK, "반려동물 수정이 정상적으로 처리되었습니다."),

    //203

    DELETE(300, HttpStatus.OK, "게시판 삭제가 정상적으로 처리되었습니다."),
    USER_DELETE(301, HttpStatus.OK, "탈퇴 처리가 정상적으로 처리되었습니다."),
    PET_DELETE(302, HttpStatus.OK, "반려동물 삭제가 정상적으로 처리되었습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}

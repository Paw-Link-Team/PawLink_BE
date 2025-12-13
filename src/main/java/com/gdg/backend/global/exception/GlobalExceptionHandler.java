package com.gdg.backend.global.exception;

import com.gdg.backend.global.code.ErrorCode;
import com.gdg.backend.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException e) {
        log.warn("UserNotFoundException", e);
        return ApiResponse.error(ErrorCode.USER_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception occurred", e); // ⭐ 핵심
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}

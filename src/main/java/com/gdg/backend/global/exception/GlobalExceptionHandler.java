package com.gdg.backend.global.exception;

import com.gdg.backend.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BedReqeustException.class)
    public ResponseEntity<ApiResponse<Void>> handlerBadRequest(BedReqeustException ex){
        return ApiResponse.error(400,ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ApiResponse.error(500, "서버 내부 오류");
    }
}

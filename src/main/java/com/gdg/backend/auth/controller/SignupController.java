package com.gdg.backend.auth.controller;

import com.gdg.backend.auth.dto.SignupRequest;
import com.gdg.backend.auth.service.AuthService;
import com.gdg.backend.global.code.ErrorCode;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.dto.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입")
@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
@Slf4j
public class SignupController {

    private final AuthService authService;

    @Operation(summary = "소셜로그인 인증 회원가입",
            description = "신규 회원의 회원가입입니다.")
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signup(
            @RequestBody SignupRequest request
    ) {
        try{
            TokenResponseDto token = authService.signUp(request);

            return ApiResponse.success(SuccessCode.USER_CREATED, token);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(ErrorCode.INVALID_TYPE);
        }
    }
}


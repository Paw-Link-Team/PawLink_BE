package com.gdg.backend.auth.controller;

import com.gdg.backend.auth.dto.AuthRequestDto;
import com.gdg.backend.auth.service.AuthService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.dto.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OIDC 로그인 및 회원가입")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인 회원가입 통합", description = "로그인 및 회원가입을 통합시켰습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signupOrLogin(
            @RequestBody AuthRequestDto request
            ){
        TokenResponseDto token = authService.signupOrLogin(request);
        return ApiResponse.success(SuccessCode.LOGIN_SUCCESS,token);
    }
}

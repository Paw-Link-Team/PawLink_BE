package com.gdg.backend.auth.controller;

import com.gdg.backend.auth.dto.LoginRequest;
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

@Tag(name = "로그인")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @Operation(summary = "기존 회원 로그인", description = "providerId와 provider으로 로그인하여 accessToken을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(
            @RequestBody LoginRequest request
    ) {
        TokenResponseDto token = authService.login(request.getProvider(), request.getProviderId());
        return ApiResponse.success(SuccessCode.LOGIN_SUCCESS, token);
    }
}

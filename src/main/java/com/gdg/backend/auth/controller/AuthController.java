package com.gdg.backend.auth.controller;

import com.gdg.backend.user.domain.Role;
import com.gdg.backend.auth.dto.kakao.KakaoTokenDto;
import com.gdg.backend.auth.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "카카오 로그인", description = "code : accessToken을 받습니다.\nstate : provider, walker, admin 3가지로 역할이 부여됩니다. 프론트는 제공자를 선택하게 만들어주세요.")
    @GetMapping("/callback/kakao")
    public KakaoTokenDto kakaoLogin(@RequestParam("code") String code,
                                    @RequestParam("state") String state) {
        String token = kakaoAuthService.getKakaoToken(code);

        Role role = Role.valueOf(state.toUpperCase());

        return kakaoAuthService.loginOrSignUp(token, role);
    }
//
//    @Operation(summary = "네이버 로그인", description = "")
//    @GetMapping("/callback/naver")
//    public KakaoTokenDto naverLogin(@RequestParam("code") String code) {}


}

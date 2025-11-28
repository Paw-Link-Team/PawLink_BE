package com.gdg.backend.auth.kakao.controller;

import com.gdg.backend.auth.kakao.dto.KakaoTokenDto;
import com.gdg.backend.auth.kakao.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "관리자 로그인", description = "카카오톡 관리자 로그인 페이지")
    @GetMapping("/callback/kakao")
    public KakaoTokenDto adminLogin(@RequestParam("code") String code) {
        String token = kakaoAuthService.getKakaoToken(code);

        return kakaoAuthService.loginOrSignUp(token);
    }


}

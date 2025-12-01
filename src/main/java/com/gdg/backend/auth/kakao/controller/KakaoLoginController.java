package com.gdg.backend.auth.kakao.controller;

import com.gdg.backend.auth.domain.User;
import com.gdg.backend.auth.kakao.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/login")
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/kakao")
    public User login(Principal principal) {
        return kakaoAuthService.kakaoLogin(principal);
    }
}

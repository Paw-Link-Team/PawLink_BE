package com.gdg.backend.auth.controller;

import com.gdg.backend.auth.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoAuthService kakaoAuthService;

}

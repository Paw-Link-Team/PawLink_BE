package com.gdg.backend.auth.controller;

import com.gdg.backend.auth.service.AuthService;
import com.gdg.backend.global.oauth.dto.AuthTokenResponse;
import com.gdg.backend.global.oauth.dto.UserInfoDto;
import com.gdg.backend.global.oauth.factory.SocialOauthServiceFactory;
import com.gdg.backend.global.oauth.service.SocialOauthService;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OauthController {

    private final SocialOauthServiceFactory serviceFactory;
    private final AuthService authService;

    /**
     * OAuth 로그인/회원가입 콜백
     * @param provider KAKAO, NAVER 등
     * @param code OAuth 인증 코드
     * @param state OAuth state (네이버만 필요)
     * @param role 회원가입 시 부여할 Role
     */
    @GetMapping("/callback/{provider}")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(
            @PathVariable String provider,
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "role", required = false, defaultValue = "ADMIN") Role role
    ) throws Exception {
        Provider providerEnum = Provider.valueOf(provider.toUpperCase());

        SocialOauthService oauthService = serviceFactory.get(providerEnum);

        UserInfoDto userInfo;
        if(providerEnum == Provider.NAVER){
            userInfo = oauthService.getUserInfo(code, state);
        } else {
            userInfo = oauthService.getUserInfo(code);
        }
        TokenResponseDto tokenResponseDto = authService.getUserInfo(userInfo, role);

        return ApiResponse.success(tokenResponseDto);
    }
}

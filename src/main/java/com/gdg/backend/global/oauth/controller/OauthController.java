package com.gdg.backend.global.oauth.controller;

import com.gdg.backend.auth.service.AuthService;
import com.gdg.backend.global.code.ErrorCode;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.exception.UserAlreadyExistsException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.oauth.dto.UserInfoDto;
import com.gdg.backend.global.oauth.factory.SocialOauthServiceFactory;
import com.gdg.backend.global.oauth.service.SocialOauthService;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.dto.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Oauth 로그인")
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
     */
    @Operation(summary = "Oauth 로그인", description = "소셜 로그인을 이용하여 accessToken을 발급받습니다.")
    @GetMapping("/callback/{provider}")
    public ResponseEntity<ApiResponse<TokenResponseDto>> singUpOrLogin(
            @PathVariable String provider,
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state
    ) throws Exception {
        try {
            Provider providerEnum = Provider.valueOf(provider.toUpperCase());

            SocialOauthService oauthService = serviceFactory.get(providerEnum);

            UserInfoDto userInfo;
            if (providerEnum == Provider.NAVER) {
                userInfo = oauthService.getUserInfo(code, state);
            } else {
                userInfo = oauthService.getUserInfo(code);
            }
            TokenResponseDto tokenResponseDto = authService.getUserInfo(userInfo, Role.USER);

            return ApiResponse.success(SuccessCode.USER_CREATED, tokenResponseDto);
        } catch (UserNotFoundException e) {
            return ApiResponse.error(ErrorCode.USER_NOT_FOUND);
        } catch (UserAlreadyExistsException e) {
            return ApiResponse.error(ErrorCode.USER_ALREADY_EXISTS);
        }
    }
}

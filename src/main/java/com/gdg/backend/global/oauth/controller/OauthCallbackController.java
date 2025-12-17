package com.gdg.backend.global.oauth.controller;

import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.global.oauth.dto.IdTokenResponse;
import com.gdg.backend.global.oauth.dto.UserInfoDto;
import com.gdg.backend.global.oauth.factory.SocialOauthServiceFactory;
import com.gdg.backend.global.oauth.service.SocialOauthService;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.domain.OauthProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Oauth를 이용하여 플랫폼 고유 아이디 가져오는 컨트롤러")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OauthCallbackController {

    private final SocialOauthServiceFactory serviceFactory;
    private final TokenProvider tokenProvider;

    @GetMapping("/callback/{provider}")
    public ResponseEntity<ApiResponse<IdTokenResponse>> callback(
            @PathVariable String provider,
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state
    ) throws Exception {

        OauthProvider oauthProviderEnum = OauthProvider.valueOf(provider.toUpperCase());
        SocialOauthService oauth = serviceFactory.get(oauthProviderEnum);

        UserInfoDto userInfo = (oauthProviderEnum == OauthProvider.NAVER)
                ? oauth.getUserInfo(code, state)
                : oauth.getUserInfo(code);

        String idToken = tokenProvider.idToken(
                userInfo.getProvider(),
                userInfo.getProviderId()
        );

        return ApiResponse.success(
                IdTokenResponse.of(idToken)
        );
    }
}


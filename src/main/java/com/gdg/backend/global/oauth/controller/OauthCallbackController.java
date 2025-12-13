package com.gdg.backend.global.oauth.controller;

import com.gdg.backend.global.oauth.dto.UserInfoDto;
import com.gdg.backend.global.oauth.factory.SocialOauthServiceFactory;
import com.gdg.backend.global.oauth.service.SocialOauthService;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OauthCallbackController {

    private final SocialOauthServiceFactory serviceFactory;
    private final UserRepository userRepository;

    @GetMapping("/callback/{provider}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> callback(
            @PathVariable String provider,
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state
    ) throws Exception {

        Provider providerEnum = Provider.valueOf(provider.toUpperCase());
        SocialOauthService oauth = serviceFactory.get(providerEnum);

        UserInfoDto userInfo = (providerEnum == Provider.NAVER)
                ? oauth.getUserInfo(code, state)
                : oauth.getUserInfo(code);

        boolean exist = userRepository
                .existsByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        if (exist) {
            return ApiResponse.success(
                    Map.of(
                            "isNewUser", false,
                            "providerId", userInfo.getProviderId(),
                            "provider", userInfo.getProvider().name()
                    )
            );
        }

        return ApiResponse.success(
                Map.of(
                        "isNewUser", true,
                        "userInfo", userInfo
                )
        );
    }
}


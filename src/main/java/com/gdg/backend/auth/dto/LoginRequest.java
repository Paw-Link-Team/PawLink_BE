package com.gdg.backend.auth.dto;

import com.gdg.backend.user.domain.OauthProvider;
import lombok.Getter;

@Getter
public class LoginRequest {
    private OauthProvider oauthProvider;
    private String providerId;
}

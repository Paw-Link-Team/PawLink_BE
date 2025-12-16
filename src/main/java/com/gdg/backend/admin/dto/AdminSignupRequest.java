package com.gdg.backend.admin.dto;

import com.gdg.backend.user.domain.OauthProvider;
import lombok.Getter;

@Getter
public class AdminSignupRequest {
    private OauthProvider oauthProvider;
    private String providerId;
}

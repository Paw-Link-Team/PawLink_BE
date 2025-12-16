package com.gdg.backend.auth.dto;

import com.gdg.backend.user.domain.OauthProvider;
import com.gdg.backend.user.domain.Type;
import lombok.Getter;

@Getter
public class SignupRequest {
    private OauthProvider oauthProvider;
    private String providerId;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private Type type;
}

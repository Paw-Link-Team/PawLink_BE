package com.gdg.backend.auth.dto;

import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import lombok.Getter;

@Getter
public class SignupRequest {
    private Provider provider;
    private String providerId;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private Type type;
}

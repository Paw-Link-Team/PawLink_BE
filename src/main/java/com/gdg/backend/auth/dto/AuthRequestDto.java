package com.gdg.backend.auth.dto;

import com.gdg.backend.user.domain.Type;
import lombok.Getter;

@Getter
public class AuthRequestDto {
    private String idToken;
    private String nickname;
    private Type type;
}

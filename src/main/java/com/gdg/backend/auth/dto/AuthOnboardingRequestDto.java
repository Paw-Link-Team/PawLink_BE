package com.gdg.backend.auth.dto;

import com.gdg.backend.user.domain.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthOnboardingRequestDto {
    @NotBlank
    private String idToken;
    @NotBlank
    private String nickname;
    @NotNull
    private Type type;
    @NotBlank
    private String phoneNumber;
}

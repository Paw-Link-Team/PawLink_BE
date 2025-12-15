package com.gdg.backend.auth.dto;

import com.gdg.backend.user.domain.Provider;
import lombok.Getter;

@Getter
public class LoginRequest {
    private Provider provider;
    private String providerId;
}

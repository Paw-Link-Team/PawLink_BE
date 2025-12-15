package com.gdg.backend.admin.dto;

import com.gdg.backend.user.domain.Provider;
import lombok.Getter;

@Getter
public class AdminSignupRequest {
    private Provider provider;
    private String providerId;
}

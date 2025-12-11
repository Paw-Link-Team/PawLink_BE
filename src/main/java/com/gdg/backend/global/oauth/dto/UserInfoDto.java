package com.gdg.backend.global.oauth.dto;

import com.gdg.backend.user.domain.Provider;

public interface UserInfoDto {
    String getProviderId();
    String getEmail();
    String getName();
    String getProfileImageUrl();
    Provider getProvider();

}

package com.gdg.backend.auth.strategy;

import com.gdg.backend.global.oauth.dto.UserInfoDto;
import com.gdg.backend.user.domain.Provider;

public interface OAuthLoginStrategy {
    UserInfoDto getUserInfo(String accessToken);
    Provider getProvider();
}

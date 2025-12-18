package com.gdg.backend.user.dto;

import com.gdg.backend.user.domain.OauthProvider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private OauthProvider oauthProvider;
    private Role role;
    private Type type;

    @Builder
    public TokenResponseDto(String accessToken,
                            String refreshToken,
                            Long userId,
                            String email,
                            String nickname,
                            String profileImageUrl,
                            OauthProvider oauthProvider,
                            Role role,
                            Type type) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.oauthProvider = oauthProvider;
        this.role = role;
        this.type = type;
    }
}

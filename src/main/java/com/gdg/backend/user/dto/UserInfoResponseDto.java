package com.gdg.backend.user.dto;

import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Role role;
    private Provider provider;
    private Type type;

    @Builder
    public UserInfoResponseDto(Long userId,
                               String email,
                               String nickname,
                               String profileImageUrl,
                               Role role,
                               Provider provider,
                               Type type){
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.provider = provider;
        this.type = type;
    }

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .provider(user.getProvider())
                .type(user.getType())
                .build();
    }
}

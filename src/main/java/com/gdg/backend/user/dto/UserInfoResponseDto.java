package com.gdg.backend.user.dto;

import com.gdg.backend.user.domain.OauthProvider;
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
    private String phoneNumber;
    private Role role;
    private OauthProvider oauthProvider;
    private Type type;

    @Builder
    public UserInfoResponseDto(Long userId,
                               String email,
                               String nickname,
                               String profileImageUrl,
                               String phoneNumber,
                               Role role,
                               OauthProvider oauthProvider,
                               Type type){
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.oauthProvider = oauthProvider;
        this.type = type;
    }

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .oauthProvider(user.getOauthProvider())
                .type(user.getType())
                .build();
    }
}

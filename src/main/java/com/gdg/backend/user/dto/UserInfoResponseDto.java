package com.gdg.backend.user.dto;

import com.gdg.backend.user.domain.Role;
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

    @Builder
    public UserInfoResponseDto(Long userId, String email, String nickname, String profileImageUrl, Role role){
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .build();
    }
}

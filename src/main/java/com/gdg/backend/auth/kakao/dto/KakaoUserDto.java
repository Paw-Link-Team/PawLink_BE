package com.gdg.backend.auth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class KakaoUserDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("email")
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }

    @Builder
    public KakaoUserDto(Long id, KakaoAccount kakaoAccount, Profile profile) {
        this.id = id;
        this.kakaoAccount = kakaoAccount;
        this.kakaoAccount.profile = profile;
    }
}

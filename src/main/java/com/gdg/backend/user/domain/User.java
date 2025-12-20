package com.gdg.backend.user.domain;

import com.gdg.backend.global.oauth.dto.UserInfoDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_oauth_provider_provider_id",
                        columnNames = {"oauth_provider", "provider_id"}
                )
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false)
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "profile_image_url", nullable = false)
    private String profileImageUrl;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    private User(
            String email,
            String nickname,
            Role role,
            Type type,
            OauthProvider oauthProvider,
            String providerId,
            String profileImageUrl
    ) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.type = type;
        this.oauthProvider = oauthProvider;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
    }

    public static User fromOAuth(UserInfoDto info, Type type) {
        return User.builder()
                .oauthProvider(info.getProvider())
                .providerId(info.getProviderId())
                .email(info.getEmail())
                .nickname(info.getName())
                .profileImageUrl(info.getProfileImageUrl())
                .type(type)
                .role(Role.USER)
                .build();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateType(Type type) {
        this.type = type;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

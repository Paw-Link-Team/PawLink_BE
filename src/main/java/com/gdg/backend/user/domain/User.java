package com.gdg.backend.user.domain;

import com.gdg.backend.global.oauth.dto.UserInfoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String profileImageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(length = 500)
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null) this.nickname = nickname;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

    public void updateType(Type type) {
        this.type = type;
    }

    public void updateRole(Role role) { this.role = role; }

    @Builder
    public User(String email,
                String nickname,
                Role role,
                Type type,
                OauthProvider oauthProvider,
                String providerId,
                String profileImageUrl) {
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


}

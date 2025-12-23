package com.gdg.backend.auth.service;

import com.gdg.backend.auth.dto.AuthLoginRequestDto;
import com.gdg.backend.auth.dto.AuthOnboardingRequestDto;
import com.gdg.backend.global.config.SuperAdminProperties;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.global.security.SignupPrincipal;
import com.gdg.backend.user.domain.OauthProvider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.domain.UserStatus;
import com.gdg.backend.user.dto.TokenResponseDto;
import com.gdg.backend.user.image.profile.ProfileImageConstants;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final SuperAdminProperties superAdminProperties;

    /**
     * 로그인 / 최초 가입
     */
    public TokenResponseDto login(AuthLoginRequestDto request) {

        SignupPrincipal principal =
                tokenProvider.parseSignupToken(request.getIdToken());

        User user = userRepository
                .findByOauthProviderAndProviderId(
                        principal.provider(),
                        principal.providerId()
                )
                .orElseGet(() -> signup(principal));

        return issueToken(user);
    }

    /**
     * 온보딩 완료
     */
    public TokenResponseDto onboarding(AuthOnboardingRequestDto request) {

        SignupPrincipal principal =
                tokenProvider.parseSignupToken(request.getIdToken());

        User user = userRepository
                .findByOauthProviderAndProviderId(
                        principal.provider(),
                        principal.providerId()
                )
                .orElseThrow(() ->
                        new IllegalStateException("존재하지 않는 사용자입니다.")
                );

        if (user.getUserStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("온보딩 대상 사용자가 아닙니다.");
        }

        user.updateNickname(request.getNickname());
        user.updateType(request.getType());
        user.updatePhoneNumber(request.getPhoneNumber());
        if(isSuperAdmin(principal.provider(), principal.providerId())){
            user.updateType(Type.ADMIN);
        }
        user.updateUserStatus(UserStatus.ACTIVE);

        return issueToken(user);
    }

    /**
     * 신규 가입 (PENDING)
     */
    private User signup(SignupPrincipal principal) {
        String tempPhoneNumber = "010-1234-5678";
        User user = User.builder()
                .oauthProvider(principal.provider())
                .providerId(principal.providerId())
                .email(principal.email())
                .nickname(generateTempNickname(principal))
                .profileImageUrl(ProfileImageConstants.DEFAULT_PROFILE_IMAGE)
                .role(Role.USER)
                .type(Type.TEMP)
                .phoneNumber(tempPhoneNumber)
                .userStatus(UserStatus.PENDING)
                .build();

        if (isSuperAdmin(principal.provider(), principal.providerId())) {
            user.updateRole(Role.SUPER_ADMIN);
        }

        return userRepository.save(user);
    }

    /**
     * 토큰 발급
     */
    private TokenResponseDto issueToken(User user) {

        String accessToken = tokenProvider.accessToken(user);
        String refreshToken = tokenProvider.refreshToken(user);

        user.updateRefreshToken(refreshToken);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .oauthProvider(user.getOauthProvider())
                .role(user.getRole())
                .type(user.getType())
                .newUser(user.getUserStatus() == UserStatus.PENDING)
                .build();
    }

    private String generateTempNickname(SignupPrincipal principal) {
        return "user_" +
                principal.provider().name().toLowerCase() +
                "_" +
                principal.providerId();
    }

    private boolean isSuperAdmin(OauthProvider provider, String providerId) {
        return provider == superAdminProperties.getOauthProvider()
                && providerId.equals(superAdminProperties.getProviderId());
    }
}

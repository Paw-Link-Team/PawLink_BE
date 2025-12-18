package com.gdg.backend.auth.service;

import com.gdg.backend.auth.dto.AuthRequestDto;
import com.gdg.backend.global.config.SuperAdminProperties;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.global.security.SignupPrincipal;
import com.gdg.backend.user.domain.OauthProvider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.dto.TokenResponseDto;
import com.gdg.backend.user.image.profile.service.ProfileImageConstants;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final SuperAdminProperties superAdminProperties;

    @Transactional
    public TokenResponseDto signupOrLogin(AuthRequestDto request){

        SignupPrincipal principal = tokenProvider.parseSignupToken(request.getIdToken());

        OauthProvider provider = principal.provider();
        String providerId = principal.providerId();
        String email = principal.email();

        return userRepository
                .findByOauthProviderAndProviderId(provider, providerId)
                .map(this::login)
                // 3️⃣ 없으면 회원가입
                .orElseGet(() -> signUp(provider, providerId, email, request));
    }

    public TokenResponseDto login(User user) {

        return saveTokenResponse(user, tokenProvider);
    }

    @Transactional
    protected TokenResponseDto signUp(
            OauthProvider provider,
            String providerId,
            String email,
            AuthRequestDto request
    ) {

        User user = User.builder()
                .oauthProvider(provider)
                .providerId(providerId)
                .email(email)
                .nickname(request.getNickname())
                .profileImageUrl(ProfileImageConstants.DEFAULT_PROFILE_IMAGE)
                .role(Role.USER)
                .type(request.getType())
                .build();

        // SUPER_ADMIN 판별
        if (isSuperAdmin(provider, providerId)) {
            user.updateRole(Role.SUPER_ADMIN);
            user.updateType(Type.ADMIN);
        }

        userRepository.save(user);

        // Refresh Token 저장
        String refreshToken = tokenProvider.refreshToken(user);
        user.updateRefreshToken(refreshToken);

        return login(user);
    }

    /**
     * 슈퍼 관리자 판별
     */
    private boolean isSuperAdmin(OauthProvider provider, String providerId) {

        return provider == superAdminProperties.getOauthProvider()
                && providerId.equals(superAdminProperties.getProviderId());
    }

    private TokenResponseDto saveTokenResponse(User user, TokenProvider tokenProvider){
        return TokenResponseDto.builder()
                .accessToken(tokenProvider.accessToken(user))
                .refreshToken(tokenProvider.refreshToken(user))
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .oauthProvider(user.getOauthProvider())
                .role(user.getRole())
                .type(user.getType())
                .build();
    }

}

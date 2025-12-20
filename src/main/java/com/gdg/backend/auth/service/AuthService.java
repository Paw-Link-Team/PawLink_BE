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
import com.gdg.backend.user.image.profile.ProfileImageConstants;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    public TokenResponseDto signupOrLogin(AuthRequestDto request) {

        SignupPrincipal principal =
                tokenProvider.parseSignupToken(request.getIdToken());

        OauthProvider provider = principal.provider();
        String providerId = principal.providerId();
        String email = principal.email();

        try {
            String tempNickname =
                    "user_" + provider.name().toLowerCase() + "_" + providerId;

            User user = User.builder()
                    .oauthProvider(provider)
                    .providerId(providerId)
                    .email(email)
                    .nickname(tempNickname)
                    .profileImageUrl(ProfileImageConstants.DEFAULT_PROFILE_IMAGE)
                    .role(Role.USER)
                    .type(request.getType())
                    .build();

            if (isSuperAdmin(provider, providerId)) {
                user.updateRole(Role.SUPER_ADMIN);
                user.updateType(Type.ADMIN);
            }

            userRepository.save(user);

            String refreshToken = tokenProvider.refreshToken(user);
            user.updateRefreshToken(refreshToken);

            return login(user);

        } catch (DataIntegrityViolationException e) {

            User existingUser = userRepository
                    .findByOauthProviderAndProviderId(provider, providerId)
                    .orElseThrow();

            return login(existingUser);
        }
    }


    public TokenResponseDto login(User user) {

        return saveTokenResponse(user, tokenProvider);
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

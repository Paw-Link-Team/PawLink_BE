package com.gdg.backend.auth.service;

import com.gdg.backend.auth.dto.SignupRequest;
import com.gdg.backend.global.config.SuperAdminProperties;
import com.gdg.backend.global.exception.UserAlreadyExistsException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.user.domain.OauthProvider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.dto.TokenResponseDto;
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

    @Transactional(readOnly = true)
    public TokenResponseDto login(OauthProvider oauthProvider, String providerId) {

        User user = userRepository.findByOauthProviderAfterAndProviderId(oauthProvider, providerId)
                .orElseThrow(() -> new UserNotFoundException("유저가 없습니다. 회원가입 진행해주세요."));

        return TokenResponseDto.builder()
                .accessToken(tokenProvider.accessToken(user))
                .refreshToken(tokenProvider.refreshToken(user))
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .oauthProvider(oauthProvider)
                .role(user.getRole())
                .type(user.getType())
                .build();
    }

    /**
     * 신규 회원 가입
     */
    @Transactional
    public TokenResponseDto signUp(SignupRequest request) {

        validateNotExists(request.getOauthProvider(),request.getProviderId());

        User user = saveUser(request);

        if (isSuperAdmin(request)) { //관리자 판별
            user.updateRole(Role.SUPER_ADMIN);
            user.updateType(Type.ADMIN);
        }

        userRepository.save(user);

        String refreshToken = tokenProvider.refreshToken(user);
        user.updateRefreshToken(refreshToken);

        return saveTokenResponse(user, tokenProvider);
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

    private void validateNotExists(OauthProvider oauthProvider, String providerId) {
        if (userRepository.existsByOauthProviderAndProviderId(oauthProvider, providerId)) {
            throw new UserAlreadyExistsException(
                    "이미 존재하는 계정입니다. 로그인을 이용해주세요."
            );
        }
    }

    private User saveUser(SignupRequest request){
        return User.builder()
                .oauthProvider(request.getOauthProvider())
                .providerId(request.getProviderId())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .profileImageUrl(request.getProfileImageUrl())
                .role(Role.USER)
                .type(request.getType())
                .build();
    }

    private boolean isSuperAdmin(SignupRequest request) {
        log.info("REQ provider={}, id={}",
                request.getOauthProvider(), request.getProviderId());

        log.info("CONF provider={}, id={}",
                superAdminProperties.getOauthProvider(),
                superAdminProperties.getProviderId());

        return request.getOauthProvider() == superAdminProperties.getOauthProvider()
                && request.getProviderId().equals(superAdminProperties.getProviderId());
    }

}

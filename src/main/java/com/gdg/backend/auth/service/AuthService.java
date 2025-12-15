package com.gdg.backend.auth.service;

import com.gdg.backend.auth.dto.SignupRequest;
import com.gdg.backend.global.config.SuperAdminProperties;
import com.gdg.backend.global.exception.UserAlreadyExistsException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
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
    public TokenResponseDto login(Provider provider, String providerId) {

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new UserNotFoundException("유저가 없습니다. 회원가입 진행해주세요."));

        return TokenResponseDto.builder()
                .accessToken(tokenProvider.accessToken(user))
                .refreshToken(tokenProvider.refreshToken(user))
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .provider(provider)
                .role(user.getRole())
                .type(user.getType())
                .build();
    }

    /**
     * 신규 회원 가입
     */
    @Transactional
    public TokenResponseDto signUp(SignupRequest request) {

        validateNotExists(request.getProvider(),request.getProviderId());

        User user = saveUser(request);

        if (isSuperAdmin(request)) { //관리자 판별
            user.updateRole(Role.SUPER_ADMIN);
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
                .provider(user.getProvider())
                .role(user.getRole())
                .type(user.getType())
                .build();
    }

    private void validateNotExists(Provider provider, String providerId) {
        if (userRepository.existsByProviderAndProviderId(provider, providerId)) {
            throw new UserAlreadyExistsException(
                    "이미 존재하는 계정입니다. 로그인을 이용해주세요."
            );
        }
    }

    private User saveUser(SignupRequest request){
        return User.builder()
                .provider(request.getProvider())
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
                request.getProvider(), request.getProviderId());

        log.info("CONF provider={}, id={}",
                superAdminProperties.getProvider(),
                superAdminProperties.getProviderId());

        return request.getProvider() == superAdminProperties.getProvider()
                && request.getProviderId().equals(superAdminProperties.getProviderId());
    }

}

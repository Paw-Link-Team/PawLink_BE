package com.gdg.backend.auth.service;

import com.gdg.backend.auth.dto.SignupRequest;
import com.gdg.backend.global.exception.UserAlreadyExistsException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.dto.TokenResponseDto;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

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
    public TokenResponseDto signUp(SignupRequest request, Type type) {

        validateNotExists(request.getProvider(),request.getProviderId());

        User user = saveUser(request, Role.USER, type);

        userRepository.save(user);

        String refreshToken = tokenProvider.refreshToken(user);
        user.updateRefreshToken(refreshToken);

        return saveTokenResponse(user, tokenProvider);
    }

    /**
       * 관리자 회원가입
     **/

    public TokenResponseDto adminSignUp(SignupRequest request){
        if (userRepository.existsByProviderAndProviderId(request.getProvider(), request.getProviderId())) {
            throw new UserAlreadyExistsException("이미 존재하는 계정입니다. 로그인을 이용해주세요.");
        }

        User user = saveUser(request, Role.ADMIN, Type.ADMIN);

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

    private User saveUser(SignupRequest request, Role role, Type type){
        return User.builder()
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .profileImageUrl(request.getProfileImageUrl())
                .role(role)
                .type(type)
                .build();
    }
}

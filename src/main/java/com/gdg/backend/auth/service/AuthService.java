package com.gdg.backend.auth.service;

import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.global.oauth.dto.UserInfoDto;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
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

    @Transactional
    public TokenResponseDto getUserInfo(UserInfoDto userInfoDto, Role role) {

        Provider provider = userInfoDto.getProvider();
        String providerId = userInfoDto.getProviderId();

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(()-> signup(userInfoDto, role));

        String accessToken = tokenProvider.accessToken(user);
        String refreshToken = tokenProvider.refreshToken(user);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .provider(provider)
                .role(role)
                .build();
    }

    private User signup(UserInfoDto dto, Role role) {
        User user = User.builder()
                .provider(dto.getProvider())
                .providerId(dto.getProviderId())
                .nickname(dto.getName())
                .email(dto.getEmail())
                .profileImageUrl(dto.getProfileImageUrl())
                .role(role)
                .build();

        return userRepository.save(user);
    }
}

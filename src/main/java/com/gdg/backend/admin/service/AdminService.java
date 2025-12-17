package com.gdg.backend.admin.service;

import com.gdg.backend.admin.dto.AdminSignupRequest;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.global.security.SignupPrincipal;
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
public class AdminService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenResponseDto createAdmin(AdminSignupRequest request){

        SignupPrincipal principal = tokenProvider.parseSignupToken(request.getIdToken());

        User user = userRepository.findByOauthProviderAndProviderId(
                principal.provider(),
                principal.providerId()
        ).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        if(user.getRole() != Role.SUPER_ADMIN){
            user.updateRole(Role.ADMIN);
        }
        user.updateType(Type.ADMIN);

        String accessToken = tokenProvider.accessToken(user);
        String refreshToken = tokenProvider.refreshToken(user);

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
                .build();
    }
}

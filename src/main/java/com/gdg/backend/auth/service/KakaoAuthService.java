package com.gdg.backend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.backend.auth.dto.kakao.KakaoTokenDto;
import com.gdg.backend.auth.dto.kakao.KakaoUserResponseDto;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.global.exception.BedReqeustException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.user.dto.TokenDto;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Value("${kakao.token-uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${kakao.user-info-uri}")
    private String KAKAO_USERINFO_URI;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.scope}")
    private List<String> scope;

    @Transactional
    public String getKakaoToken(String code){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            params.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URI, request, String.class);

        if(response.getStatusCode().is2xxSuccessful()){
            String json = response.getBody();
            try {
                ObjectMapper mapper = new ObjectMapper();

                TokenDto dto = mapper.readValue(json, TokenDto.class);

                return dto.getAccessToken();
            } catch (Exception e) {
                throw new BedReqeustException("카카오 토큰 파싱을 실패했습니다.\nerror message: " + e.getMessage());
            }
        }
        throw new BedReqeustException("카카오 토큰 값을 가져온는데 실패했습니다.");
    }

    @Transactional
    public KakaoTokenDto loginOrSignUp(String getKakaoToken, Role role) {
        KakaoUserResponseDto kakaoUserDto = getKakaoUser(getKakaoToken);

        if ((kakaoUserDto.getKakaoAccount() == null || kakaoUserDto.getKakaoAccount().getProfile() == null
                || kakaoUserDto.getKakaoAccount().getEmail() == null)
                && kakaoUserDto.getProperties() == null) {
            throw new UserNotFoundException("유저 정보가 없습니다.");
        }

        User user = saveUser(kakaoUserDto, role);

        String accessToken = tokenProvider.accessToken(user);
        String refreshToken = tokenProvider.refreshToken(user);

        String joinScope = String.join(" ", scope);

        return KakaoTokenDto.builder()
                .tokenType("Bearer ")
                .accessToken(accessToken)
                .idToken(getKakaoToken)
                .expiresIn(tokenProvider.getAccessTokenValiditySeconds()/1000)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(tokenProvider.getRefreshTokenValiditySeconds()/1000)
                .scope(joinScope)
                .build();
    }

    private User saveUser(KakaoUserResponseDto kakaoUserDto, Role role) {
        User user = userRepository.findByEmail(kakaoUserDto.getKakaoAccount().getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(kakaoUserDto.getKakaoAccount().getEmail())
                        .nickname(kakaoUserDto.getKakaoAccount().getProfile().getNickname())
                        .role(role)
                        .provider(Provider.KAKAO)
                        .providerId(kakaoUserDto.getId().toString())
                        .build()
                ));
        return userRepository.save(user);
    }

    private KakaoUserResponseDto getKakaoUser(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(KAKAO_USERINFO_URI));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        if(response.getStatusCode().is2xxSuccessful()){
            String json = response.getBody();
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                return objectMapper.readValue(json, KakaoUserResponseDto.class);
            } catch (Exception e) {
                throw new UserNotFoundException("유저 정보를 가져오는데 실패했습니다.");
            }
        }

        throw new UserNotFoundException("유저 정보를 가져오는데 실패했습니다.");
    }
}

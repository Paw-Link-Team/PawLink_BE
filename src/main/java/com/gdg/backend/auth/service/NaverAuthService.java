package com.gdg.backend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.backend.global.oauth.dto.provider.NaverTokenDto;
import com.gdg.backend.global.oauth.dto.provider.NaverUserResponseDto;
import com.gdg.backend.global.exception.BedReqeustException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.jwt.TokenProvider;
import com.gdg.backend.user.domain.Provider;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.User;
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

@Service
@RequiredArgsConstructor
public class NaverAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.token-uri}")
    private String tokenUri;

    @Value("${naver.user-info-uri}")
    private String userInfoUri;

    @Transactional
    public String getNaverAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("client_secret", clientSecret);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String json = response.getBody();
            try {
                ObjectMapper mapper = new ObjectMapper();

                NaverTokenDto dto = mapper.readValue(json, NaverTokenDto.class);
                return dto.getAccessToken();
            } catch (Exception e) {
                throw new BedReqeustException("네이버 토큰 파싱을 실패했습니다.\nerror message: " + e.getMessage());
            }
        }
        throw new BedReqeustException("네이버 토큰 값을 가져온는데 실패했습니다.");
    }

    @Transactional
    public NaverTokenDto naverLoginOrSignUp(String getNaverToken) {
        NaverUserResponseDto naverUserResponseDto = getNaverUser(getNaverToken);

        if (naverUserResponseDto == null || naverUserResponseDto.getResponse().getEmail() == null) {
            throw new UserNotFoundException("네이버 계정 이메일을 확인할 수 없습니다..");
        }

        String email = naverUserResponseDto.getResponse().getEmail();
        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null) {
            user = User.builder()
                    .email(email)
                    .nickname(naverUserResponseDto.getResponse().getNickname())
                    .provider(Provider.NAVER)
                    .providerId(naverUserResponseDto.getResponse().getId())
                    .role(Role.WALKER)
                    .build();

            userRepository.save(user);
        }

        String accessToken = tokenProvider.accessToken(user);
        String refreshToken = tokenProvider.refreshToken(user);

        return NaverTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer ")
                .expiresIn(tokenProvider.getAccessTokenValiditySeconds() / 1000)
                .error(null)
                .errorDescription(null)
                .build();
    }

    private NaverUserResponseDto getNaverUser(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(userInfoUri));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            String json = response.getBody();
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                return objectMapper.readValue(json, NaverUserResponseDto.class);
            } catch (Exception e) {
                throw new UserNotFoundException("유저 정보를 가져오는데 실패했습니다.");
            }
        }

        throw new UserNotFoundException("유저 정보를 가져오는데 실패했습니다.");
    }
}


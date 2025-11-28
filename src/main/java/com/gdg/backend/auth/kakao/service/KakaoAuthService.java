package com.gdg.backend.auth.kakao.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.backend.auth.domain.Provider;
import com.gdg.backend.auth.domain.Role;
import com.gdg.backend.auth.domain.User;
import com.gdg.backend.auth.exception.BedReqeustException;
import com.gdg.backend.auth.exception.UserNotFoundException;
import com.gdg.backend.auth.jwt.TokenProvider;
import com.gdg.backend.auth.kakao.dto.KakaoTokenDto;
import com.gdg.backend.auth.kakao.dto.KakaoUserDto;
import com.gdg.backend.auth.repository.UserRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.Principal;

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

    public String getKakaoToken(String code){
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("redirect_uri: " + redirectUri);

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
        System.out.println("TOKEN RESPONSE = " + response.getBody());

        if(response.getStatusCode().is2xxSuccessful()){
            String json = response.getBody();
            try {
                ObjectMapper mapper = new ObjectMapper();

                KakaoTokenDto dto = mapper.readValue(json, KakaoTokenDto.class);

                return dto.getAccessToken();
            } catch (Exception e) {
                throw new BedReqeustException("카카오 토큰 파싱을 실패했습니다.\nerror message: " + e.getMessage());
            }
        }
        throw new BedReqeustException("카카오 토큰 값을 가져온는데 실패했습니다.");
    }

    public KakaoTokenDto loginOrSignUp(String getKakaoToken) {
        KakaoUserDto kakaoUserDto = getKakaoUser(getKakaoToken);

        if ((kakaoUserDto.getKakaoAccount() == null || kakaoUserDto.getKakaoAccount().getProfile() == null
                || kakaoUserDto.getKakaoAccount().getEmail() == null)
                && kakaoUserDto.getProperties() == null) {
            throw new UserNotFoundException("유저 정보가 없습니다.");
        }


        User user = saveUser(kakaoUserDto);

        userRepository.save(user);

        return tokenProvider.createToken(user);
    }

    private User saveUser(KakaoUserDto kakaoUserDto){
        return userRepository.findByEmail(kakaoUserDto.getKakaoAccount().getEmail())
                .orElseGet(() ->userRepository.save(User.builder()
                .email(kakaoUserDto.getKakaoAccount().getEmail())
                .nickname(kakaoUserDto.getKakaoAccount().getProfile().getNickname())
                .profileImage(kakaoUserDto.getKakaoAccount().getProfile().getProfileImageUrl())
                .thumbnailImageUrl(kakaoUserDto.getKakaoAccount().getProfile().getThumbnailImageUrl())
                .role(Role.ADMIN)
                .provider(Provider.KAKAO)
                .providerId(kakaoUserDto.getId())
                .build()
                )
        );
    }

    private KakaoUserDto getKakaoUser(String accessToken){
        System.out.println("accessToken: " + accessToken);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(KAKAO_USERINFO_URI));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        System.out.println("TOKEN RESPONSE = " + response.getBody());
        if(response.getStatusCode().is2xxSuccessful()){
            String json = response.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, KakaoUserDto.class);
        }

        throw new UserNotFoundException("유저 정보를 가져오는데 실패했습니다.");
    }

    public User kakaoLogin(Principal principal) {
        Long id = Long.parseLong(principal.getName());

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
    }
}

//package com.gdg.backend.auth.strategy;
//
//import com.gdg.backend.global.oauth.dto.UserInfoDto;
//import com.gdg.backend.user.domain.Provider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//@Component
//@RequiredArgsConstructor
//public class KakaoLoginStrategy implements OAuthLoginStrategy{
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Override
//    UserInfoDto getUserInfo(String accessToken){
//        return null;
//    }
//
//    @Override
//    Provider getProvider(){
//        return Provider.KAKAO;
//    }
//}

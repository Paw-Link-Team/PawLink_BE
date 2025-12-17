package com.gdg.backend.global.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class IdTokenResponse {

    private String idToken;

    public static IdTokenResponse of(String idToken){
        return IdTokenResponse.builder().idToken(idToken).build();
    }

}

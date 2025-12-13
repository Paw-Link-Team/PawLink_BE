package com.gdg.backend.user.dto;

import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String nickname;
    private String profileImageUrl;
    private Type type;
}

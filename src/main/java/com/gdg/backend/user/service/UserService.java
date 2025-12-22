package com.gdg.backend.user.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.Type;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.dto.UserInfoResponseDto;
import com.gdg.backend.user.dto.UserUpdateRequestDto;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoResponseDto getMyInfo(Long userId){
        User user = getUser(userId);

        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequestDto request){
        User user = getUser(userId);

        user.updateProfile(request.getNickname(), request.getProfileImageUrl());

        if (request.getType() != null &&
                request.getType() != user.getType()) {

            validateTypeChange(user, request.getType());
            user.updateType(request.getType());
        }
    }

    @Transactional
    public void deleteUser(Long userId){
        User user = getUser(userId);

        userRepository.delete(user);
    }

    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateTypeChange(User user, Type targetType){
        if(user.getType() == targetType){
            throw new IllegalStateException("똑같은 타입입니다.");
        }

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("관리자는 타입을 변경할 수 없습니다.");
        }

        if (user.getType() == Type.WALKER &&
                targetType == Type.OWNER) {
            return;
        }

        if (user.getType() == Type.OWNER &&
                targetType == Type.WALKER) {
            return;
        }

        throw new IllegalStateException("허용되지 않은 타입 변경입니다.");
    }
}

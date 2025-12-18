package com.gdg.backend.user.service;

import com.gdg.backend.global.exception.ProfileImageUploadException;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.image.profile.service.ProfileImageConstants;
import com.gdg.backend.user.image.profile.service.ProfileImageService;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final ProfileImageService profileImageService;

    @Transactional
    public void updateProfileImage(Long userId, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        // 새 이미지 업로드
        String newImageUrl = profileImageService.uploadProfileImage(image, userId);

        // 기존 이미지 삭제
        profileImageService.deleteIfExists(user.getProfileImageUrl());

        // User 엔티티 업데이트
        user.updateProfileImage(newImageUrl);
    }

    @Transactional
    public void deleteProfileImage(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        String currentImage = user.getProfileImageUrl();

        if (ProfileImageConstants.DEFAULT_PROFILE_IMAGE.equals(currentImage)) {
            return;
        }

        profileImageService.deleteIfExists(currentImage);

        user.updateProfileImage(ProfileImageConstants.DEFAULT_PROFILE_IMAGE);
    }
}

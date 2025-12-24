package com.gdg.backend.user.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.image.profile.ProfileImageConstants;
import com.gdg.backend.user.image.profile.ProfileImageService;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final ProfileImageService profileImageService;

    public void updateProfileImage(Long userId, MultipartFile image) {

        if (image == null || image.isEmpty()) {
            return;
        }

        User user = findUser(userId);

        String newImageUrl =
                profileImageService.uploadProfileImage(image, userId);

        deleteIfNotDefault(user.getProfileImageUrl());

        user.updateProfileImage(newImageUrl);
    }

    public void deleteProfileImage(Long userId) {

        User user = findUser(userId);

        deleteIfNotDefault(user.getProfileImageUrl());

        user.updateProfileImage(ProfileImageConstants.DEFAULT_PROFILE_IMAGE);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("유저를 찾을 수 없습니다.")
                );
    }

    private void deleteIfNotDefault(String imageUrl) {
        if (!ProfileImageConstants.DEFAULT_PROFILE_IMAGE.equals(imageUrl)) {
            profileImageService.deleteIfExists(imageUrl);
        }
    }
}

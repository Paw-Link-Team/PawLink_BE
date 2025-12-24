package com.gdg.backend.user.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/image")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PatchMapping(
            value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<Object>> updateProfileImage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("image") MultipartFile image
    ) {
        userProfileService.updateProfileImage(principal.userId(), image);
        return ApiResponse.success(SuccessCode.IMAGE_UPDATE);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Object>> deleteProfileImage(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userProfileService.deleteProfileImage(principal.userId());
        return ApiResponse.success(SuccessCode.IMAGE_DELETE);
    }
}

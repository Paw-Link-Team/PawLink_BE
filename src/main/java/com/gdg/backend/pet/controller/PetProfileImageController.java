package com.gdg.backend.pet.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.pet.service.PetProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pet")
public class PetProfileImageController {

    private final PetProfileImageService petProfileImageService;

    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<String>> uploadPetProfileImage(
            @RequestPart("image") MultipartFile image
    ) {
        String imageUrl = petProfileImageService.upload(image);
        return ApiResponse.success(SuccessCode.OK, imageUrl);
    }

}

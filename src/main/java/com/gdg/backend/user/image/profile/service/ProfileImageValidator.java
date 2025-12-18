package com.gdg.backend.user.image.profile.service;

import com.gdg.backend.global.exception.InvalidProfileImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ProfileImageValidator {

    private static final long MAX_SIZE = 5 * 1024 * 1024; //5MB
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public void validate(MultipartFile file){
        if(file==null || file.isEmpty()){
            throw new InvalidProfileImageException("이미지 파일이 비어 있습니다.");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidProfileImageException("허용되지 않은 이미지 형식입니다.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new InvalidProfileImageException("이미지 용량은 5MB를 초과할 수 없습니다.");
        }
    }
}

package com.gdg.backend.user.image.profile.service;

import com.gdg.backend.global.exception.ProfileImageUploadException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;


import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {

    private static final String PROFILE_DIR = "profile/";
    private static final String S3_URL_PREFIX =
            "https://%s.s3.ap-northeast-2.amazonaws.com/";


    private final S3Client s3Client;
    private final ProfileImageValidator profileImageValidator;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadProfileImage(MultipartFile file, Long userId) {
        profileImageValidator.validate(file);

        String key = createKey(file, userId);

        try (InputStream inputStream = file.getInputStream()) {

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );

            return createImageUrl(key);

        } catch (IOException e) {
            throw new ProfileImageUploadException(
                    "프로필 이미지 업로드 중 오류가 발생했습니다.", e
            );
        } catch (S3Exception e) {
            throw new ProfileImageUploadException(
                    "S3 업로드에 실패했습니다.", e
            );
        }
    }

    public void deleteIfExists(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String prefix = String.format(S3_URL_PREFIX, bucket);
        if (!imageUrl.startsWith(prefix)) {
            return;
        }

        String key = imageUrl.substring(prefix.length());

        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(bucket)
                    .key(key)
            );
        } catch (S3Exception e) {
            log.warn("Failed to delete profile image. key={}", key, e);
        }
    }




    private String getExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf("."));
    }

    private String createKey(MultipartFile file, Long userId) {
        return PROFILE_DIR + userId + "/" + UUID.randomUUID() + getExtension(file);
    }

    private String createImageUrl(String key) {
        return String.format(S3_URL_PREFIX, bucket) + key;
    }
}

package com.gdg.backend.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetProfileImageService {

    private static final String PET_PROFILE_DIR = "pet/profile/";

    private static final String S3_URL_PREFIX =
            "https://%s.s3.ap-northeast-2.amazonaws.com/";

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 없습니다.");
        }

        String fileName = PET_PROFILE_DIR + UUID.randomUUID()
                + "_" + image.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(image.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            image.getInputStream(),
                            image.getSize()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return String.format(S3_URL_PREFIX, bucket) + fileName;
    }

    /**
     * 반려견 프로필 이미지 삭제 (선택)
     */
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String key = extractKey(imageUrl);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    private String extractKey(String imageUrl) {
        return imageUrl.substring(
                imageUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length()
        );
    }
}

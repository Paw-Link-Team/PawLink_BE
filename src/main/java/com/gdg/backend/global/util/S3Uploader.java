package com.gdg.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /* =====================
     * 업로드
     * ===================== */
    public String upload(MultipartFile file, String dir) {
        validateFile(file);

        String key = dir + "/" + createFileName(file.getOriginalFilename());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return getFileUrl(key);

        } catch (IOException e) {
            throw new IllegalStateException("S3 파일 업로드 실패", e);
        }
    }

    /* =====================
     * 삭제 🔥 (추가된 핵심)
     * ===================== */
    public void delete(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

        } catch (Exception e) {
            throw new IllegalStateException("S3 파일 삭제 실패: " + imageUrl, e);
        }
    }

    /* =====================
     * 내부 유틸
     * ===================== */

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }

    private String createFileName(String originalName) {
        String ext = extractExt(originalName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractExt(String filename) {
        int idx = filename.lastIndexOf(".");
        if (idx == -1) {
            throw new IllegalArgumentException("확장자가 없는 파일입니다.");
        }
        return filename.substring(idx + 1);
    }

    /**
     * https://bucket.s3.region.amazonaws.com/dir/file.jpg
     * → dir/file.jpg
     */
    private String extractKeyFromUrl(String imageUrl) {
        URI uri = URI.create(imageUrl);
        return uri.getPath().substring(1); // 맨 앞 '/' 제거
    }

    private String getFileUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}

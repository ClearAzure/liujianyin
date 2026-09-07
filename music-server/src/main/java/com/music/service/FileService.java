package com.music.service;

import com.music.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String uploadMusic(MultipartFile file) {
        return upload(file, minioConfig.getBucketMusic(), "music/");
    }

    public String uploadCover(MultipartFile file) {
        return upload(file, minioConfig.getBucketCover(), "cover/");
    }

    public String uploadLyric(MultipartFile file) {
        return upload(file, minioConfig.getBucketLyric(), "lyric/");
    }

    private String upload(MultipartFile file, String bucket, String prefix) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String objectName = prefix + UUID.randomUUID().toString().substring(0, 8) + ext;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioConfig.getEndpoint() + "/" + bucket + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}

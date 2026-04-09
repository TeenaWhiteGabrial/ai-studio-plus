package com.aistudio.service.service.impl;

import com.aistudio.service.config.OssConfig.OssProperties;
import com.aistudio.service.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;

/**
 * 浪潮云 OSS 服务实现 (S3 兼容, AWS SDK 2.x)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final S3Client s3Client;
    private final OssProperties ossProperties;

    @Override
    public String generateUpToken(String keyPrefix) {
        // S3 不需要预生成 token
        return "";
    }

    @Override
    public String generateUploadToken(String key) {
        // S3 不需要预生成 token
        return "";
    }

    @Override
    public void deleteFile(String key) {
        if (key == null || key.isBlank()) return;
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(ossProperties.getBucketName())
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
            log.info("删除 OSS 文件成功: key={}", key);
        } catch (Exception e) {
            log.warn("删除 OSS 文件失败（忽略）: key={}, error={}", key, e.getMessage());
        }
    }

    @Override
    public String getDomain() {
        return ossProperties.getDownloadUrl();
    }

    @Override
    public String getPublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return ossProperties.getDownloadUrl() + "/" + key;
    }

    @Override
    public String generatePresignedUrl(String key, int expireSeconds) {
        if (key == null || key.isBlank()) {
            return null;
        }
        try (S3Presigner presigner = createPresigner()) {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(req -> req
                            .bucket(ossProperties.getBucketName())
                            .key(key)
                            .build())
                    .signatureDuration(Duration.ofSeconds(expireSeconds))
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("生成预签名 URL 失败: key={}, error={}", key, e.getMessage());
            return getPublicUrl(key);
        }
    }

    /**
     * 上传文件到 OSS
     */
    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        try {
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(ossProperties.getBucketName())
                    .key(key)
                    .acl(software.amazon.awssdk.services.s3.model.ObjectCannedACL.PUBLIC_READ);

            if (contentType != null && !contentType.isEmpty()) {
                requestBuilder.contentType(contentType);
            }

            PutObjectRequest request = requestBuilder.build();
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));

            log.info("上传文件成功: key={}", key);
            return key;
        } catch (Exception e) {
            log.error("上传文件失败: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("上传文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 S3Presigner（用于生成预签名 URL）
     */
    private S3Presigner createPresigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                ossProperties.getAccessKey(), ossProperties.getSecretKey());

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .region(Region.of(ossProperties.getRegion()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}

package com.aistudio.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * 浪潮云 OSS 配置 (S3 兼容模式, AWS SDK 2.x)
 */
@Configuration
@EnableConfigurationProperties(OssConfig.OssProperties.class)
public class OssConfig {

    @Data
    @ConfigurationProperties(prefix = "oss")
    public static class OssProperties {
        /**
         * OSS 接入域名/Endpoint
         */
        private String endpoint;

        /**
         * Access Key ID
         */
        private String accessKey;

        /**
         * Access Key Secret
         */
        private String secretKey;

        /**
         * Bucket 名称
         */
        private String bucketName;

        /**
         * 下载域名
         */
        private String downloadUrl;

        /**
         * 区域
         */
        private String region = "sd-jn-scyd-gyhlwzq-icp";
    }

    @Bean
    public S3Client s3Client(OssProperties props) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                props.getAccessKey(), props.getSecretKey());

        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true)  // 启用路径样式访问
                .build();

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(s3Config)
                .build();
    }
}

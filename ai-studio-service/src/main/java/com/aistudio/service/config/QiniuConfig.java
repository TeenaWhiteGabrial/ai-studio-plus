package com.aistudio.service.config;

import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QiniuConfig.QiniuProperties.class)
public class QiniuConfig {

    @Data
    @ConfigurationProperties(prefix = "qiniu")
    public static class QiniuProperties {
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;
        private int tokenExpire = 3600;
    }

    @Bean
    public Auth qiniuAuth(QiniuProperties props) {
        return Auth.create(props.getAccessKey(), props.getSecretKey());
    }
}

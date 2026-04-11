package com.aistudio.service.service.impl;

import com.aistudio.service.config.QiniuConfig.QiniuProperties;
import com.aistudio.service.service.OssService;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final Auth qiniuAuth;
    private final QiniuProperties qiniuProperties;

    @Override
    public String generateUpToken(String keyPrefix) {
        return qiniuAuth.uploadToken(
                qiniuProperties.getBucket(),
                null,
                qiniuProperties.getTokenExpire(),
                null,
                true
        );
    }

    @Override
    public void deleteFile(String key) {
        if (key == null || key.isBlank()) return;
        try {
            Configuration cfg = new Configuration(Region.autoRegion());
            BucketManager bucketManager = new BucketManager(qiniuAuth, cfg);
            bucketManager.delete(qiniuProperties.getBucket(), key);
        } catch (Exception e) {
            log.warn("删除 OSS 文件失败（忽略）: key={}, error={}", key, e.getMessage());
        }
    }

    @Override
    public String getDomain() {
        return qiniuProperties.getDomain();
    }
}

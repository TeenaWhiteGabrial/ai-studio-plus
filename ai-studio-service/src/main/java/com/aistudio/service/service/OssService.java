package com.aistudio.service.service;

import java.io.InputStream;

public interface OssService {
    /**
     * 生成上传凭证
     * @param keyPrefix 文件 key 前缀（可选）
     * @return token（阿里云 S3 返回空字符串）
     */
    String generateUpToken(String keyPrefix);

    /**
     * 上传文件到 OSS
     * @param key OSS 中的文件路径
     * @param inputStream 输入流
     * @param contentLength 文件大小
     * @param contentType MIME 类型
     * @return OSS key
     */
    String uploadFile(String key, InputStream inputStream, long contentLength, String contentType);

    /**
     * 生成上传凭证（指定完整 key）
     * @param key 文件 key
     * @return token（阿里云 S3 返回空字符串）
     */
    String generateUploadToken(String key);

    /**
     * 删除 OSS 文件
     * @param key 文件 key
     */
    void deleteFile(String key);

    /**
     * 删除文件（别名）
     */
    default void delete(String key) {
        deleteFile(key);
    }

    /**
     * 获取 OSS 下载域名
     */
    String getDomain();

    /**
     * 获取文件公开访问 URL
     */
    String getPublicUrl(String key);

    /**
     * 生成预签名 URL（用于下载）
     * @param key 文件 key
     * @param expireSeconds 过期时间（秒）
     * @return 预签名 URL
     */
    String generatePresignedUrl(String key, int expireSeconds);
}

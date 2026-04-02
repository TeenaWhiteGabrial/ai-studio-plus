package com.aistudio.service.service;

public interface OssService {
    /**
     * 生成七牛云上传凭证
     * @param keyPrefix 文件 key 前缀（可选）
     * @return UpToken
     */
    String generateUpToken(String keyPrefix);

    /**
     * 删除七牛云文件
     * @param key 文件 key
     */
    void deleteFile(String key);

    /**
     * 获取七牛云域名
     */
    String getDomain();
}

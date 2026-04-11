package com.aistudio.service.gitlab;

import java.util.List;

/**
 * GitLab API 客户端接口
 */
public interface GitLabClient {

    /**
     * 测试 GitLab 连接
     */
    boolean testConnection();

    /**
     * 获取仓库文件树
     *
     * @param path   目录路径
     * @param recursive 是否递归
     * @return 文件列表
     */
    List<GitLabTreeItem> getRepositoryTree(String path, boolean recursive);

    /**
     * 获取文件内容和提交信息
     *
     * @param filePath 文件路径
     * @return 文件信息
     */
    GitLabFile getFile(String filePath);

    /**
     * 获取文件 Raw 内容
     *
     * @param filePath 文件路径
     * @return 文件内容字符串
     */
    String getRawContent(String filePath);

    /**
     * 获取文件 Raw 内容（二进制）
     *
     * @param filePath 文件路径
     * @return 文件内容字节数组
     */
    byte[] getRawContentBytes(String filePath);

    /**
     * 获取仓库信息
     */
    GitLabRepository getRepository();
}

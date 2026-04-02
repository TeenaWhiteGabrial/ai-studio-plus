package com.aistudio.service.gitlab;

import lombok.Data;

/**
 * GitLab 文件信息
 */
@Data
public class GitLabFile {
    private String filePath;
    private String content;     // Base64 编码的内容
    private String contentPlain; // 解码后的内容
    private String commitId;
    private String lastCommitId;
    private Long size;
}

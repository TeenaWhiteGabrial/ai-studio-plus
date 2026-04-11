package com.aistudio.service.gitlab;

import lombok.Data;

/**
 * GitLab 仓库树形结构项
 */
@Data
public class GitLabTreeItem {
    private String id;
    private String name;
    private String path;
    private String type;  // blob (文件) 或 tree (目录)
    private String mode;
}

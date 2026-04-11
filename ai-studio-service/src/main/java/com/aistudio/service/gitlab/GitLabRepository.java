package com.aistudio.service.gitlab;

import lombok.Data;

/**
 * GitLab 仓库信息
 */
@Data
public class GitLabRepository {
    private Long id;
    private String name;
    private String path;
    private String webUrl;
    private String defaultBranch;
    private boolean isPrivate;
}

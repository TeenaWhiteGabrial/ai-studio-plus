package com.aistudio.service.gitlab;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "skill.gitlab")
public class GitLabProperties {

    /**
     * GitLab 服务器 URL
     */
    private String url;

    /**
     * GitLab 访问 Token（Deploy Token 或 Personal Access Token）
     */
    private String token;

    /**
     * 仓库名称（如 ai-studio/skills）
     */
    private String repository = "7023";

    /**
     * 分支名称
     */
    private String branch = "release";

    /**
     * Skill 文件在仓库中的基础路径
     */
    private String basePath = "Skills/";

    /**
     * 是否启用 GitLab 集成
     */
    private boolean enabled = true;

    /**
     * API 请求超时时间（毫秒）
     */
    private int timeout = 30000;
}

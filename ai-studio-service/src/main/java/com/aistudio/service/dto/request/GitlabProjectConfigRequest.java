package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GitlabProjectConfigRequest {

    @NotNull(message = "关联项目不能为空")
    @JsonAlias("projectId")
    private Long project_id;

    @NotNull(message = "GitLab 项目ID不能为空")
    @JsonAlias("gitlabProjectId")
    private Long gitlab_project_id;

    @NotBlank(message = "GitLab 项目名称不能为空")
    @JsonAlias("gitlabProjectName")
    private String gitlab_project_name;

    @JsonAlias("gitlabProjectPath")
    private String gitlab_project_path;

    @JsonAlias("gitlabGroupName")
    private String gitlab_group_name;

    private Integer enabled;
}

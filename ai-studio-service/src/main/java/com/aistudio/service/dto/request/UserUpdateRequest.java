package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @JsonAlias("realName")
    private String realName;

    @JsonAlias("gitName")
    @Size(max = 50, message = "git_name length can not exceed 50")
    private String gitName;

    @JsonAlias("deptId")
    private Long deptId;

    @JsonAlias("teamId")
    private Long teamId;

    private String email;
    private String avatar;
    private Integer status;
}

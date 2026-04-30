package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {

    @NotBlank(message = "username can not be blank")
    private String username;

    @JsonAlias("gitName")
    @Size(max = 50, message = "git_name length can not exceed 50")
    private String gitName;

    @NotBlank(message = "password can not be blank")
    private String password;

    @JsonAlias("realName")
    private String realName;

    @JsonAlias("deptId")
    private Long deptId;

    @JsonAlias("teamId")
    private Long teamId;

    private String email;
    private String avatar;

    @JsonAlias("roleIds")
    private List<Long> roleIds;
}

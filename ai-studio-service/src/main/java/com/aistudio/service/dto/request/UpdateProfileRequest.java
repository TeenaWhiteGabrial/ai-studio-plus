package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Email(message = "invalid email format")
    @Size(max = 100, message = "email length can not exceed 100")
    private String email;

    @JsonAlias("gitName")
    @Size(max = 50, message = "git_name length can not exceed 50")
    private String gitName;

    @Size(max = 500, message = "avatar length can not exceed 500")
    private String avatar;
}

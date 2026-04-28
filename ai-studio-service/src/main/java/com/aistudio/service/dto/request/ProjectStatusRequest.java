package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectStatusRequest {

    @NotBlank
    @JsonAlias("status")
    private String status;
}

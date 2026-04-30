package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OpenOutputSubmitRequest {

    @NotBlank(message = "git_name can not be blank")
    @JsonAlias({"username", "user_name", "gitName"})
    private String gitName;

    @JsonAlias("statDate")
    private LocalDate statDate;

    @JsonAlias("projectId")
    private Long projectId;

    @JsonAlias("projectRootName")
    private String projectRootName;

    @JsonAlias("outputType")
    private String outputType;

    @JsonAlias("resourceId")
    private Long resourceId;

    @JsonAlias("prdDocCount")
    private Integer prdDocCount = 0;

    @JsonAlias("dataModelDocCount")
    private Integer dataModelDocCount = 0;

    @JsonAlias("apiDocCount")
    private Integer apiDocCount = 0;

    @JsonAlias("javaFileCount")
    private Integer javaFileCount = 0;

    @JsonAlias("javaCodeLines")
    private Integer javaCodeLines = 0;

    @JsonAlias("apiCount")
    private Integer apiCount = 0;

    @JsonAlias("coreBizServiceCount")
    private Integer coreBizServiceCount = 0;

    @JsonAlias("entityCount")
    private Integer entityCount = 0;

    @JsonAlias("frontendComponentCount")
    private Integer frontendComponentCount = 0;

    @JsonAlias("frontendPageCount")
    private Integer frontendPageCount = 0;

    @JsonAlias("frontendCommonComponentCount")
    private Integer frontendCommonComponentCount = 0;

    @JsonAlias("tsCodeLines")
    private Integer tsCodeLines = 0;

    @JsonAlias("frontendCodeLines")
    private Integer frontendCodeLines = 0;

    @JsonAlias("sqlScriptCount")
    private Integer sqlScriptCount = 0;

    @JsonAlias("testFileCount")
    private Integer testFileCount = 0;

    @JsonAlias("totalCodeLines")
    private Integer totalCodeLines = 0;

    private String remark;
}

package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 开放 API 产出提交请求
 */
@Data
public class OpenOutputSubmitRequest {

    @NotBlank(message = "username 不能为空")
    private String username;

    private LocalDate statDate;

    // 文档类指标
    private Integer prdDocCount = 0;
    private Integer dataModelDocCount = 0;
    private Integer apiDocCount = 0;

    // Java 后端指标
    private Integer javaFileCount = 0;
    private Integer javaCodeLines = 0;
    private Integer apiCount = 0;
    private Integer coreBizServiceCount = 0;
    private Integer entityCount = 0;

    // 前端指标
    private Integer frontendComponentCount = 0;
    private Integer frontendPageCount = 0;
    private Integer frontendCommonComponentCount = 0;
    private Integer tsCodeLines = 0;
    private Integer frontendCodeLines = 0;

    // 其他指标
    private Integer sqlScriptCount = 0;
    private Integer testFileCount = 0;
    private Integer totalCodeLines = 0;

    private String remark;

    /**
     * 项目根目录名（可选参数，用于标识产出所属项目）
     */
    private String projectRootName;
}

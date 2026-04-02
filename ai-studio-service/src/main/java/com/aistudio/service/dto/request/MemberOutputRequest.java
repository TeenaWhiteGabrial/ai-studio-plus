package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberOutputRequest {

    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;

    // 原有字段（保留兼容）
    private Integer prdCount = 0;
    private Integer apiCount = 0;
    private Integer javaLines = 0;
    private Integer frontendLines = 0;

    // 新增 16 个指标字段
    // 文档类指标
    private Integer prdDocCount = 0;
    private Integer dataModelDocCount = 0;
    private Integer apiDocCount = 0;

    // Java 后端指标
    private Integer javaFileCount = 0;
    private Integer javaCodeLines = 0;
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
}

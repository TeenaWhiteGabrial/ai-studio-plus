package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_output")
public class MemberOutput {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String realName;  // 姓名（关联查询）
    @TableField(exist = false)
    private String department;  // 部门（关联查询）
    private LocalDate statDate;

    // 原有字段（保留兼容）
    private Integer prdCount;
    private Integer javaLines;
    private Integer frontendLines;

    // 新增 16 个指标字段
    // 文档类指标
    private Integer prdDocCount;
    private Integer dataModelDocCount;
    private Integer apiDocCount;

    // Java 后端指标
    private Integer javaFileCount;
    private Integer javaCodeLines;
    private Integer apiCount;
    private Integer coreBizServiceCount;
    private Integer entityCount;

    // 前端指标
    private Integer frontendComponentCount;
    private Integer frontendPageCount;
    private Integer frontendCommonComponentCount;
    private Integer tsCodeLines;
    private Integer frontendCodeLines;

    // 其他指标
    private Integer sqlScriptCount;
    private Integer testFileCount;
    private Integer totalCodeLines;

    private String remark;
    private String projectRootName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

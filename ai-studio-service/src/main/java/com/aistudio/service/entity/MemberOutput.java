package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    private String gitName;

    private Long projectId;
    private String projectRootName;
    private LocalDate statDate;
    private String outputType;
    private Long resourceId;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String department;

    private Integer prdDocCount;
    private Integer dataModelDocCount;
    private Integer apiDocCount;

    private Integer javaFileCount;
    private Integer javaCodeLines;
    private Integer apiCount;
    private Integer coreBizServiceCount;
    private Integer entityCount;

    private Integer frontendComponentCount;
    private Integer frontendPageCount;
    private Integer frontendCommonComponentCount;
    private Integer tsCodeLines;
    private Integer frontendCodeLines;

    private Integer sqlScriptCount;
    private Integer testFileCount;
    private Integer totalCodeLines;

    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

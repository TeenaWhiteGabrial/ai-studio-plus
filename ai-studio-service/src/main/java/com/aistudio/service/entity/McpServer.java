package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mcp_server")
public class McpServer {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String serverType;
    private String apiEndpoint;
    private String authType;
    private String configJson;

    // 版本管理
    private Long latestVersionId;
    private String latestVersion;
    private Integer totalVersions;

    // 审核信息
    private Integer status;            // 0-待审核 1-通过 2-拒绝
    private LocalDateTime reviewTime;    // 审核时间
    private String reviewComment;       // 审核备注
    private Long createdBy;
    private Long creatorId;
    private Long deptId;

    // 连接测试
    private LocalDateTime lastTestAt;
    private String lastTestResult;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

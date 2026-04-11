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
    private String apiEndpoint;
    private String authType;
    private String configJson;
    private Integer status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

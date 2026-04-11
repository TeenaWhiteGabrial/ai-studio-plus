package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("plugin")
public class Plugin {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String type;
    private String version;
    private String fileOssKey;
    private String fileUrl;
    private Long fileSize;
    private Integer status;
    private Integer downloadCount;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

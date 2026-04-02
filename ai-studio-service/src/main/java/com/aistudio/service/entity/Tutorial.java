package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tutorial")
public class Tutorial {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String category;
    private String tags;
    private String contentOssKey;
    private String contentUrl;
    private Integer status;
    private Integer viewCount;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

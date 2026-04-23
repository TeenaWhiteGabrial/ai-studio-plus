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
    private String description;
    private String category;
    private String coverImage;
    private String contentType;        // richText/markdown
    private String content;            // 富文本或Markdown内容
    private String videoUrl;           // 视频OSS地址
    private String zipFileUrl;         // 附件ZIP OSS地址
    private String zipFileName;        // 附件文件名
    private Integer status;            // 0-待审核 1-通过 2-拒绝
    private Long creatorId;
    private LocalDateTime reviewTime;
    private String reviewComment;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 版本控制相关字段
    private Long latestVersionId;
    private String latestVersion;
    private Integer totalVersions;
}

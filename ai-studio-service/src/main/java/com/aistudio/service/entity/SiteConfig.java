package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("site_config")
public class SiteConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String siteName;
    private String siteDescription;
    private String logoUrl;
    private String iconUrl;
    private String footerText;
    private String footerCopyright;
    private String footerRecord;
    private String footerLinks;
    private String contacts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

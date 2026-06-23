package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_model_config")
public class AiModelConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String providerName;
    private String baseUrl;
    private String modelName;
    private String apiKey;
    private String promptVersion;
    private Integer requestTimeoutMs;
    private Integer maxRetries;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

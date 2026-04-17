package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核请求
 */
@Data
public class AuditRequest {

    @NotNull(message = "审核状态不能为空")
    private Integer status;         // 1-通过 2-拒绝

    private String reviewComment;    // 审核备注
}

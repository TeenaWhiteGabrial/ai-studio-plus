package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class EmailReportRuleRequest {

    @NotBlank(message = "规则名称不能为空")
    private String name;

    @JsonAlias({"toRecipientUserIds", "to_recipient_user_ids", "recipientUserIds", "recipient_user_ids"})
    private List<Long> toRecipientUserIds;

    @JsonAlias({"ccRecipientUserIds", "cc_recipient_user_ids"})
    private List<Long> ccRecipientUserIds;

    @JsonAlias("recipients")
    private List<String> recipients;

    @JsonAlias({"userIds", "user_ids"})
    private List<Long> userIds;

    @JsonAlias({"sendTime", "send_time"})
    @NotBlank(message = "发送时间不能为空")
    private String sendTime;

    private Integer status;
}

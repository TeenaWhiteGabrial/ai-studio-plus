package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ActivityStatGroupRequest {

    @NotBlank(message = "统计组名称不能为空")
    @JsonAlias("groupName")
    private String group_name;

    @NotBlank(message = "统计组标识不能为空")
    @JsonAlias("groupKey")
    private String group_key;

    private String description;

    @JsonAlias("scopeType")
    private String scope_type;

    private Integer enabled;

    @JsonAlias("userIds")
    private List<Long> user_ids;
}

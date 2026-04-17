package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuRequest {

    @NotBlank(message = "菜单名称不能为空")
    private String name;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private String permission;

    private Integer sort;

    private Integer hidden;
}

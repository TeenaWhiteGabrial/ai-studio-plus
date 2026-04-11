package com.aistudio.service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MenuTreeVO {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private String permission;
    private Integer sort;
    private Integer hidden;
    private List<MenuTreeVO> children;
}

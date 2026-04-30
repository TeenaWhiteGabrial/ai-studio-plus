package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String gitName;
    private Long deptId;
    private String email;
    private Integer status;
    private String avatar;          // 头像URL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<Long> roleIds;

    @TableField(exist = false)
    private String deptName;  // 部门名称（关联查询）

    private Long teamId;  // 团队ID

    @TableField(exist = false)
    private String teamName;  // 团队名称（关联查询）
}

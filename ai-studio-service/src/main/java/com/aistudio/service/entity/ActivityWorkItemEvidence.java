package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_work_item_evidence")
public class ActivityWorkItemEvidence {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long workItemId;
    private String evidenceType;
    private String evidenceRef;
    private String summary;
    private LocalDateTime createdAt;
}

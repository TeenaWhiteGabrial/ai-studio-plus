package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyTaskRequest {

    @NotNull(message = "所属项目不能为空")
    @JsonAlias({"projectId", "project_id"})
    private Long projectId;

    @NotNull(message = "任务日期不能为空")
    @JsonAlias({"taskDate", "task_date"})
    private LocalDate taskDate;

    @NotBlank(message = "任务内容不能为空")
    private String content;

    @NotNull(message = "任务工时不能为空")
    @DecimalMin(value = "0.0", message = "任务工时不能小于 0")
    @DecimalMax(value = "24.0", message = "任务工时不能大于 24")
    private BigDecimal hours;

    @NotNull(message = "AI 辅助程度不能为空")
    @JsonAlias({"aiParticipation", "ai_participation"})
    private Integer aiParticipation;

    @JsonAlias({"outputId", "output_id"})
    private Long outputId;

    private String status;
}

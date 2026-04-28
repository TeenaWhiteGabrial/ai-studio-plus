package com.aistudio.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DailyTaskStatsVO {
    private Long weekCompletedCount;
    private BigDecimal weekHours;
    private Long monthCompletedCount;
    private BigDecimal monthHours;
}

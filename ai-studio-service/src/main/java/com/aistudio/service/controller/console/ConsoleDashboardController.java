package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.entity.DailyTask;
import com.aistudio.service.entity.Project;
import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.DailyTaskMapper;
import com.aistudio.service.mapper.ProjectMapper;
import com.aistudio.service.mapper.SkillMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Console - 数据看板")
@RestController
@RequestMapping("/console/dashboard")
@RequiredArgsConstructor
public class ConsoleDashboardController {

    private static final String PROJECT_STATUS_ACTIVE = "ACTIVE";

    private final SecurityUtils securityUtils;
    private final SkillMapper skillMapper;
    private final DailyTaskMapper dailyTaskMapper;
    private final ProjectMapper projectMapper;
    private final SysUserMapper userMapper;

    @Operation(summary = "Console 首页数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Long userId = securityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<DailyTask> todayTasks = dailyTaskMapper.selectList(new LambdaQueryWrapper<DailyTask>()
                .eq(DailyTask::getUserId, userId)
                .eq(DailyTask::getTaskDate, today));
        List<DailyTask> monthTasks = dailyTaskMapper.selectList(new LambdaQueryWrapper<DailyTask>()
                .eq(DailyTask::getUserId, userId)
                .between(DailyTask::getTaskDate, monthStart, today));
        List<DailyTask> recentTasks = dailyTaskMapper.selectList(new LambdaQueryWrapper<DailyTask>()
                .eq(DailyTask::getUserId, userId)
                .orderByDesc(DailyTask::getTaskDate)
                .orderByDesc(DailyTask::getCreatedAt)
                .last("LIMIT 5"));

        Map<String, Object> result = new HashMap<>();
        result.put("skillCount", skillMapper.selectCount(new LambdaQueryWrapper<Skill>()
                .eq(Skill::getCreatorId, userId)
                .and(wrapper -> wrapper.eq(Skill::getIsDeleted, 0).or().isNull(Skill::getIsDeleted))));
        result.put("skillMonthCount", skillMapper.selectCount(new LambdaQueryWrapper<Skill>()
                .eq(Skill::getCreatorId, userId)
                .ge(Skill::getCreatedAt, monthStart.atStartOfDay())
                .and(wrapper -> wrapper.eq(Skill::getIsDeleted, 0).or().isNull(Skill::getIsDeleted))));
        result.put("taskCount", todayTasks.size());
        result.put("todayTaskHours", sumHours(todayTasks));
        result.put("projectCount", countCurrentTeamActiveProjects(userId));
        result.put("monthTaskHours", sumHours(monthTasks));
        result.put("recentTasks", recentTasks.stream().map(this::toTaskMap).toList());
        return Result.success(result);
    }

    private BigDecimal sumHours(List<DailyTask> tasks) {
        return tasks.stream()
                .map(DailyTask::getHours)
                .filter(hours -> hours != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long countCurrentTeamActiveProjects(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getTeamId() == null) {
            return 0L;
        }
        return projectMapper.selectCount(new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, PROJECT_STATUS_ACTIVE)
                .eq(Project::getTeamId, user.getTeamId()));
    }

    private Map<String, Object> toTaskMap(DailyTask task) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("projectId", task.getProjectId());
        map.put("projectName", getProjectName(task.getProjectId()));
        map.put("taskDate", task.getTaskDate());
        map.put("content", task.getContent());
        map.put("hours", task.getHours());
        map.put("status", task.getStatus());
        map.put("createdAt", task.getCreatedAt());
        return map;
    }

    private String getProjectName(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = projectMapper.selectById(projectId);
        return project == null ? null : project.getProjectName();
    }
}

package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.DailyTaskRequest;
import com.aistudio.service.dto.response.DailyTaskStatsVO;
import com.aistudio.service.entity.DailyTask;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.entity.Project;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.DailyTaskMapper;
import com.aistudio.service.mapper.MemberOutputMapper;
import com.aistudio.service.mapper.ProjectMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.DailyTaskService;
import com.aistudio.service.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DailyTaskServiceImpl implements DailyTaskService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String PROJECT_STATUS_ACTIVE = "ACTIVE";
    private static final Set<Integer> AI_ASSISTANCE_SCORES = Set.of(0, 25, 50, 75, 90);

    private final DailyTaskMapper dailyTaskMapper;
    private final MemberOutputMapper memberOutputMapper;
    private final ProjectMapper projectMapper;
    private final SysUserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public List<DailyTask> list(Long userId, LocalDate startDate, LocalDate endDate, String status) {
        LambdaQueryWrapper<DailyTask> wrapper = new LambdaQueryWrapper<DailyTask>()
                .eq(DailyTask::getUserId, userId)
                .orderByAsc(DailyTask::getTaskDate)
                .orderByDesc(DailyTask::getCreatedAt);
        if (startDate != null && endDate != null) {
            wrapper.between(DailyTask::getTaskDate, startDate, endDate);
        } else if (startDate != null) {
            wrapper.ge(DailyTask::getTaskDate, startDate);
        } else if (endDate != null) {
            wrapper.le(DailyTask::getTaskDate, endDate);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(DailyTask::getStatus, status);
        }

        List<DailyTask> tasks = dailyTaskMapper.selectList(wrapper);
        tasks.forEach(this::fillOutput);
        return tasks;
    }

    @Override
    public DailyTask create(Long userId, DailyTaskRequest request) {
        validateRequest(userId, request);
        DailyTask task = new DailyTask();
        task.setUserId(userId);
        applyRequest(task, request);
        dailyTaskMapper.insert(task);
        fillOutput(task);
        notificationService.createRuleNotification(
                userId,
                "TASK_ASSIGNED",
                "任务已登记到你名下",
                "你有一条新的每日任务：" + task.getContent(),
                task.getId(),
                "daily_task",
                "TASK_ASSIGNED:" + task.getId() + ":" + userId
        );
        return task;
    }

    @Override
    public DailyTask update(Long userId, Long id, DailyTaskRequest request) {
        validateRequest(userId, request);
        DailyTask task = getOwnedTask(userId, id);
        applyRequest(task, request);
        dailyTaskMapper.updateById(task);
        fillOutput(task);
        return task;
    }

    @Override
    public void delete(Long userId, Long id) {
        DailyTask task = getOwnedTask(userId, id);
        dailyTaskMapper.deleteById(task.getId());
    }

    @Override
    public DailyTaskStatsVO stats(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monthStart = today.withDayOfMonth(1);
        List<DailyTask> weekTasks = list(userId, weekStart, today, STATUS_COMPLETED);
        List<DailyTask> monthTasks = list(userId, monthStart, today, STATUS_COMPLETED);
        return new DailyTaskStatsVO(
                (long) weekTasks.size(),
                sumHours(weekTasks),
                (long) monthTasks.size(),
                sumHours(monthTasks));
    }

    private void validateRequest(Long userId, DailyTaskRequest request) {
        Project project = projectMapper.selectById(request.getProjectId());
        if (project == null || !PROJECT_STATUS_ACTIVE.equals(project.getStatus())) {
            throw new BusinessException("所属项目不存在或不是进行中状态");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getTeamId() == null) {
            throw new BusinessException("当前用户未分配团队，无法选择项目");
        }
        if (project.getTeamId() == null || !user.getTeamId().equals(project.getTeamId())) {
            throw new BusinessException("只能选择本团队的项目");
        }
        if (!AI_ASSISTANCE_SCORES.contains(request.getAiParticipation())) {
            throw new BusinessException("AI 辅助程度只能选择无 AI 参与、轻度辅助、中度辅助、高度辅助、AI 主导");
        }
        if (request.getOutputId() != null) {
            MemberOutput output = memberOutputMapper.selectById(request.getOutputId());
            if (output == null || !userId.equals(output.getUserId())) {
                throw new BusinessException("关联产出记录不存在或无权限访问");
            }
        }
    }

    private DailyTask getOwnedTask(Long userId, Long id) {
        DailyTask task = dailyTaskMapper.selectById(id);
        if (task == null || !userId.equals(task.getUserId())) {
            throw new BusinessException("任务不存在或无权限访问");
        }
        return task;
    }

    private void applyRequest(DailyTask task, DailyTaskRequest request) {
        task.setProjectId(request.getProjectId());
        task.setTaskDate(request.getTaskDate());
        task.setContent(request.getContent().trim());
        task.setHours(request.getHours());
        task.setAiParticipation(request.getAiParticipation());
        task.setOutputId(request.getOutputId());
        String status = request.getStatus();
        task.setStatus(status == null || status.isBlank() ? STATUS_PENDING : status);
        if (STATUS_COMPLETED.equals(task.getStatus()) && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        if (!STATUS_COMPLETED.equals(task.getStatus())) {
            task.setCompletedAt(null);
        }
    }

    private void fillOutput(DailyTask task) {
        if (task.getOutputId() != null) {
            task.setOutput(memberOutputMapper.selectById(task.getOutputId()));
        }
    }

    private BigDecimal sumHours(List<DailyTask> tasks) {
        return tasks.stream()
                .map(DailyTask::getHours)
                .filter(hours -> hours != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

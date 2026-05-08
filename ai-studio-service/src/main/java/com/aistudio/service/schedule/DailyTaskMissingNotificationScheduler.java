package com.aistudio.service.schedule;

import com.aistudio.service.entity.DailyTask;
import com.aistudio.service.entity.NotificationRule;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.entity.UserNotificationRuleGroup;
import com.aistudio.service.mapper.DailyTaskMapper;
import com.aistudio.service.mapper.NotificationRuleMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.mapper.UserNotificationRuleGroupMapper;
import com.aistudio.service.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyTaskMissingNotificationScheduler {

    private static final String RULE_TYPE = "DAILY_TASK_MISSING";

    private final NotificationRuleMapper ruleMapper;
    private final UserNotificationRuleGroupMapper userRuleGroupMapper;
    private final SysUserMapper userMapper;
    private final DailyTaskMapper dailyTaskMapper;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void remindMissingYesterdayTask() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<NotificationRule> rules = ruleMapper.selectList(new LambdaQueryWrapper<NotificationRule>()
                .eq(NotificationRule::getRuleType, RULE_TYPE)
                .eq(NotificationRule::getEnabled, 1));
        if (rules.isEmpty()) return;

        Set<Long> groupIds = rules.stream().map(NotificationRule::getGroupId).collect(Collectors.toSet());
        List<UserNotificationRuleGroup> bindings = userRuleGroupMapper.selectList(new LambdaQueryWrapper<UserNotificationRuleGroup>()
                .in(UserNotificationRuleGroup::getGroupId, groupIds));
        for (UserNotificationRuleGroup binding : bindings) {
            SysUser user = userMapper.selectById(binding.getUserId());
            if (user == null || !Integer.valueOf(1).equals(user.getStatus())) continue;
            Long taskCount = dailyTaskMapper.selectCount(new LambdaQueryWrapper<DailyTask>()
                    .eq(DailyTask::getUserId, user.getId())
                    .eq(DailyTask::getTaskDate, yesterday));
            if (taskCount > 0) continue;
            String name = user.getRealName() == null || user.getRealName().isBlank() ? user.getUsername() : user.getRealName();
            notificationService.createRuleNotification(
                    user.getId(),
                    RULE_TYPE,
                    "昨日任务未录入提醒",
                    name + "，你昨日还没有录入每日任务，请及时补充。",
                    null,
                    "daily_task",
                    RULE_TYPE + ":" + user.getId() + ":" + yesterday
            );
        }
        log.info("昨日任务未录入提醒检查完成: date={}", yesterday);
    }
}

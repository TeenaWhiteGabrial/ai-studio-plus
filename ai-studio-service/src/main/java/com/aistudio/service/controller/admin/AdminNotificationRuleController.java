package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.entity.NotificationRule;
import com.aistudio.service.entity.NotificationRuleGroup;
import com.aistudio.service.entity.UserNotificationRuleGroup;
import com.aistudio.service.mapper.NotificationRuleGroupMapper;
import com.aistudio.service.mapper.NotificationRuleMapper;
import com.aistudio.service.mapper.UserNotificationRuleGroupMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/notification-rule")
@RequiredArgsConstructor
public class AdminNotificationRuleController {

    private static final List<String> DEFAULT_RULE_TYPES = List.of(
            "DAILY_TASK_MISSING",
            "TASK_ASSIGNED",
            "ARTICLE_LIKED",
            "ARTICLE_COMMENTED",
            "ARTICLE_TAKEN_DOWN",
            "RESOURCE_APPROVED",
            "RESOURCE_TAKEN_DOWN"
    );

    private final NotificationRuleGroupMapper groupMapper;
    private final NotificationRuleMapper ruleMapper;
    private final UserNotificationRuleGroupMapper userGroupMapper;

    @GetMapping("/groups")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<NotificationRuleGroup>> groups() {
        return Result.success(groupMapper.selectList(new LambdaQueryWrapper<NotificationRuleGroup>()
                .orderByDesc(NotificationRuleGroup::getIsDefault)
                .orderByAsc(NotificationRuleGroup::getId)));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    @Transactional
    public Result<Long> createGroup(@RequestBody NotificationRuleGroup request) {
        request.setId(null);
        request.setIsDefault(0);
        request.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        groupMapper.insert(request);
        ensureRules(request.getId());
        return Result.success(request.getId());
    }

    @PostMapping("/groups/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> updateGroup(@PathVariable Long id, @RequestBody NotificationRuleGroup request) {
        NotificationRuleGroup current = requireGroup(id);
        current.setName(request.getName());
        current.setDescription(request.getDescription());
        current.setStatus(request.getStatus());
        groupMapper.updateById(current);
        return Result.success();
    }

    @PostMapping("/groups/{id}/delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    @Transactional
    public Result<Void> deleteGroup(@PathVariable Long id) {
        NotificationRuleGroup current = requireGroup(id);
        if (Integer.valueOf(1).equals(current.getIsDefault())) {
            throw new BusinessException(400, "默认规则组不可删除");
        }
        userGroupMapper.delete(new LambdaQueryWrapper<UserNotificationRuleGroup>().eq(UserNotificationRuleGroup::getGroupId, id));
        ruleMapper.delete(new LambdaQueryWrapper<NotificationRule>().eq(NotificationRule::getGroupId, id));
        groupMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/groups/{id}/rules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<NotificationRule>> rules(@PathVariable Long id) {
        ensureRules(id);
        return Result.success(ruleMapper.selectList(new LambdaQueryWrapper<NotificationRule>()
                .eq(NotificationRule::getGroupId, id)
                .orderByAsc(NotificationRule::getId)));
    }

    @PostMapping("/groups/{id}/rules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    @Transactional
    public Result<Void> updateRules(@PathVariable Long id, @RequestBody List<NotificationRule> rules) {
        requireGroup(id);
        ensureRules(id);
        for (NotificationRule rule : rules) {
            ruleMapper.update(null, new LambdaUpdateWrapper<NotificationRule>()
                    .eq(NotificationRule::getGroupId, id)
                    .eq(NotificationRule::getRuleType, rule.getRuleType())
                    .set(NotificationRule::getEnabled, rule.getEnabled())
                    .set(NotificationRule::getConfigJson, rule.getConfigJson()));
        }
        return Result.success();
    }

    @GetMapping("/groups/{id}/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Long>> groupUsers(@PathVariable Long id) {
        return Result.success(userGroupMapper.selectList(new LambdaQueryWrapper<UserNotificationRuleGroup>()
                .eq(UserNotificationRuleGroup::getGroupId, id)
        ).stream().map(UserNotificationRuleGroup::getUserId).toList());
    }

    @PostMapping("/groups/{id}/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    @Transactional
    public Result<Void> bindUsers(@PathVariable Long id, @RequestBody Map<String, List<Long>> request) {
        requireGroup(id);
        List<Long> userIds = request.getOrDefault("userIds", List.of());
        userGroupMapper.delete(new LambdaQueryWrapper<UserNotificationRuleGroup>().eq(UserNotificationRuleGroup::getGroupId, id));
        for (Long userId : userIds.stream().distinct().toList()) {
            userGroupMapper.delete(new LambdaQueryWrapper<UserNotificationRuleGroup>().eq(UserNotificationRuleGroup::getUserId, userId));
            UserNotificationRuleGroup binding = new UserNotificationRuleGroup();
            binding.setUserId(userId);
            binding.setGroupId(id);
            userGroupMapper.insert(binding);
        }
        return Result.success();
    }

    private NotificationRuleGroup requireGroup(Long id) {
        NotificationRuleGroup group = groupMapper.selectById(id);
        if (group == null) throw new BusinessException(404, "规则组不存在");
        return group;
    }

    private void ensureRules(Long groupId) {
        for (String ruleType : DEFAULT_RULE_TYPES) {
            if (!ruleMapper.exists(new LambdaQueryWrapper<NotificationRule>()
                    .eq(NotificationRule::getGroupId, groupId)
                    .eq(NotificationRule::getRuleType, ruleType))) {
                NotificationRule rule = new NotificationRule();
                rule.setGroupId(groupId);
                rule.setRuleType(ruleType);
                rule.setEnabled(1);
                ruleMapper.insert(rule);
            }
        }
    }
}

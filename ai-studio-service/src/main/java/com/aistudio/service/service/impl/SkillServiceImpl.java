package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.SkillCreateRequest;
import com.aistudio.service.dto.request.SkillQuery;
import com.aistudio.service.dto.request.SkillUpdateRequest;
import com.aistudio.service.dto.request.SkillVersionRequest;
import com.aistudio.service.dto.request.SkillVersionRequest.VersionBumpType;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.SkillDetailVO;
import com.aistudio.service.dto.response.SkillVersionVO;
import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SkillVersion;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.SkillMapper;
import com.aistudio.service.mapper.SkillVersionMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.NotificationService;
import com.aistudio.service.service.SkillService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Skill 服务实现（OSS + 版本控制模式）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillMapper skillMapper;
    private final SkillVersionMapper versionMapper;
    private final SysUserMapper userMapper;
    private final OssService ossService;
    private final NotificationService notificationService;

    // ========== 核心 CRUD ==========

    @Override
    @Transactional
    public Long createSkill(SkillCreateRequest request, Long userId, Long deptId) {
        // 检查名称唯一性
        if (skillMapper.existsByName(request.getName())) {
            throw new BusinessException("技能名称已存在");
        }

        // 创建 skill 主记录
        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCategory(request.getCategory());
        skill.setCreatorId(userId);
        skill.setDeptId(deptId);
        skill.setStatus(0);  // 待审核
        skill.setIsDeleted(0);
        skill.setDownloadCount(0);
        skill.setTotalVersions(0);
        skillMapper.insert(skill);

        // 发布初始版本 1.0.0
        createVersion(skill.getId(), "1.0.0", request.getOssKey(),
                request.getFileSize(), request.getChangelog(), userId);

        log.info("创建技能成功: {}, id={}, userId={}", skill.getName(), skill.getId(), userId);
        return skill.getId();
    }

    @Override
    @Transactional
    public String publishVersion(Long skillId, SkillVersionRequest request,
                                  Long userId, Long deptId, List<String> roles) {
        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);
        checkCanEdit(skill, userId, roles);

        // 生成新版本号
        String currentVersion = skill.getLatestVersion();
        String newVersion = generateNextVersion(currentVersion, request.getBumpType());

        // 检查版本是否已存在
        if (versionMapper.existsBySkillIdAndVersion(skillId, newVersion)) {
            throw new BusinessException("版本 " + newVersion + " 已存在");
        }

        // 创建版本
        createVersion(skillId, newVersion, request.getOssKey(),
                request.getFileSize(), request.getChangelog(), userId);

        log.info("发布版本成功: skillId={}, version={}, userId={}", skillId, newVersion, userId);
        return newVersion;
    }

    @Override
    @Transactional
    public void updateSkill(Long skillId, SkillUpdateRequest request, Long userId, List<String> roles) {
        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);
        checkCanEdit(skill, userId, roles);

        // 只更新基础信息
        if (StringUtils.hasText(request.getDescription())) {
            skill.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getCategory())) {
            skill.setCategory(request.getCategory());
        }

        skillMapper.updateById(skill);
        log.info("更新技能成功: skillId={}, userId={}", skillId, userId);
    }

    @Override
    @Transactional
    public void deleteSkill(Long skillId, Long userId, List<String> roles) {
        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);
        checkCanDelete(skill, userId, roles);

        // 软删除
        skill.setIsDeleted(1);
        skill.setDeletedAt(LocalDateTime.now());
        skill.setDeletedBy(userId);
        skillMapper.updateById(skill);

        log.info("删除技能成功: skillId={}, userId={}", skillId, userId);
        // 注意：OSS 文件不删除，保留历史版本
    }

    @Override
    @Transactional
    public void deleteVersion(Long skillId, String version, Long userId, List<String> roles) {
        // 仅超管可删除版本
        if (!isSuperAdmin(roles)) {
            throw new BusinessException("无权删除版本");
        }

        SkillVersion v = versionMapper.selectBySkillIdAndVersion(skillId, version);
        if (v == null) {
            throw new BusinessException("版本不存在");
        }

        // 删除 OSS 文件
        try {
            ossService.delete(v.getOssKey());
        } catch (Exception e) {
            log.warn("删除 OSS 文件失败: {}", v.getOssKey(), e);
            // 继续删除数据库记录
        }

        // 删除数据库记录
        versionMapper.deleteById(v.getId());

        // 如果删除的是最新版本，需要更新主表
        Skill skill = skillMapper.selectById(skillId);
        if (v.getId().equals(skill.getLatestVersionId())) {
            SkillVersion latest = versionMapper.selectLatestBySkillId(skillId);
            skill.setLatestVersionId(latest != null ? latest.getId() : null);
            skill.setLatestVersion(latest != null ? latest.getVersion() : null);
            skill.setTotalVersions(Math.max(0, skill.getTotalVersions() - 1));
            skillMapper.updateById(skill);
        } else {
            // 只更新版本计数
            skill.setTotalVersions(Math.max(0, skill.getTotalVersions() - 1));
            skillMapper.updateById(skill);
        }

        log.info("删除版本成功: skillId={}, version={}, userId={}", skillId, version, userId);
    }

    @Override
    @Transactional
    public void auditSkill(Long skillId, AuditRequest request, Long userId, List<String> roles) {
        // 只有 SUPER_ADMIN 和 OP_ADMIN 有审核权限
        if (!isAdmin(roles) && !isSuperAdmin(roles)) {
            throw new BusinessException("无权审核技能");
        }

        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);

        skill.setStatus(request.getStatus());
        skill.setReviewTime(LocalDateTime.now());
        skill.setReviewComment(request.getReviewComment());
        skillMapper.updateById(skill);

        notifyResourceAuditResult(skill.getCreatorId(), "Skill", skill.getName(), skillId, request.getStatus());

        log.info("审核技能成功: skillId={}, status={}, userId={}", skillId, request.getStatus(), userId);
    }

    private void notifyResourceAuditResult(Long creatorId, String resourceType, String resourceName, Long resourceId, Integer status) {
        if (creatorId == null || status == null) return;
        if (status == 1) {
            notificationService.createRuleNotification(
                    creatorId,
                    "RESOURCE_APPROVED",
                    "资源审核通过",
                    "你的" + resourceType + "《" + resourceName + "》已审核通过。",
                    resourceId,
                    resourceType.toLowerCase(),
                    "RESOURCE_APPROVED:" + resourceType + ":" + resourceId + ":" + status
            );
        } else if (status == 2) {
            notificationService.createRuleNotification(
                    creatorId,
                    "RESOURCE_TAKEN_DOWN",
                    "资源未通过或已下架",
                    "你的" + resourceType + "《" + resourceName + "》未通过审核或已被下架。",
                    resourceId,
                    resourceType.toLowerCase(),
                    "RESOURCE_TAKEN_DOWN:" + resourceType + ":" + resourceId + ":" + status
            );
        }
    }

    // ========== 查询 ==========

    @Override
    public SkillDetailVO getSkillDetail(Long skillId, Long userId, List<String> roles) {
        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);

        SkillDetailVO vo = convertToDetailVO(skill);

        // 根据权限返回版本列表
        List<SkillVersionVO> versions = getVersionsByPermission(skillId, userId, roles);
        vo.setVersions(versions);

        return vo;
    }

    @Override
    public List<SkillVersionVO> getSkillVersions(Long skillId, Long userId, List<String> roles) {
        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);

        return getVersionsByPermission(skillId, userId, roles);
    }

    @Override
    public SkillVersion getSkillVersion(Long skillId, String version) {
        return versionMapper.selectBySkillIdAndVersion(skillId, version);
    }

    @Override
    public PageResult<SkillDetailVO> listSkills(SkillQuery query, Long userId, Long deptId, List<String> roles) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();

        // 只查询未删除的
        wrapper.eq(Skill::getIsDeleted, 0);

        // 关键词搜索
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Skill::getName, query.getKeyword());
        }

        // 分类筛选
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(Skill::getCategory, query.getCategory());
        }

        // 排序
        wrapper.orderByDesc(Skill::getUpdatedAt);

        Page<Skill> page = skillMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        // 转换为 VO
        List<SkillDetailVO> records = page.getRecords().stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), records);
    }

    // ========== 下载 ==========

    @Override
    public String downloadSkill(Long skillId, String version, Long userId, List<String> roles) {
        Skill skill = skillMapper.selectById(skillId);
        checkSkillExists(skill);

        String targetVersion = (version == null || "latest".equals(version))
                ? skill.getLatestVersion() : version;

        if (targetVersion == null) {
            throw new BusinessException("暂无可用版本");
        }

        // 非超管只能下载最新版本
        if (!targetVersion.equals(skill.getLatestVersion()) && !isSuperAdmin(roles)) {
            throw new BusinessException("无权下载此版本");
        }

        SkillVersion v = versionMapper.selectBySkillIdAndVersion(skillId, targetVersion);
        if (v == null) {
            throw new BusinessException("版本不存在");
        }

        // 增加下载次数
        skillMapper.incrementDownloadCount(skillId);

        // 返回预签名 URL（5分钟有效）
        return ossService.generatePresignedUrl(v.getOssKey(), 300);
    }

    // ========== 权限检查 ==========

    @Override
    public boolean canEdit(Skill skill, Long userId, List<String> roles) {
        if (isAdmin(roles) || isSuperAdmin(roles)) {
            return true;
        }
        return skill.getCreatorId().equals(userId);
    }

    @Override
    public boolean canDelete(Skill skill, Long userId, List<String> roles) {
        return canEdit(skill, userId, roles);
    }

    @Override
    public boolean canViewAllVersions(Long userId, List<String> roles) {
        return isSuperAdmin(roles);
    }

    // ========== 内部方法 ==========

    /**
     * 创建版本记录
     */
    private void createVersion(Long skillId, String version, String ossKey,
                               Long fileSize, String changelog, Long userId) {
        SkillVersion.VersionParts parts = SkillVersion.parseVersion(version);

        SkillVersion v = new SkillVersion();
        v.setSkillId(skillId);
        v.setVersion(version);
        v.setMajor(parts.major());
        v.setMinor(parts.minor());
        v.setPatch(parts.patch());
        v.setVersionNumber(parts.toNumber());
        v.setOssKey(ossKey);
        v.setOssUrl(ossService.getPublicUrl(ossKey));
        v.setFileSize(fileSize);
        v.setChangelog(changelog);
        v.setCreatedBy(userId);
        versionMapper.insert(v);

        // 更新主表
        Skill skill = skillMapper.selectById(skillId);
        skill.setLatestVersionId(v.getId());
        skill.setLatestVersion(version);
        skill.setTotalVersions((skill.getTotalVersions() == null ? 0 : skill.getTotalVersions()) + 1);
        skillMapper.updateById(skill);
    }

    /**
     * 生成下一个版本号
     */
    private String generateNextVersion(String currentVersion, VersionBumpType bumpType) {
        if (currentVersion == null) {
            return "1.0.0";
        }

        String[] parts = currentVersion.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        return switch (bumpType) {
            case MAJOR -> (major + 1) + ".0.0";
            case MINOR -> major + "." + (minor + 1) + ".0";
            case PATCH -> major + "." + minor + "." + (patch + 1);
        };
    }

    /**
     * 根据权限获取版本列表
     */
    private List<SkillVersionVO> getVersionsByPermission(Long skillId, Long userId, List<String> roles) {
        if (isSuperAdmin(roles)) {
            // 超管：查看所有版本
            List<SkillVersion> versions = versionMapper.selectBySkillId(skillId);
            return versions.stream()
                    .map(this::convertToVersionVO)
                    .collect(Collectors.toList());
        } else {
            // 其他人：只查看最新版本
            SkillVersion latest = versionMapper.selectLatestBySkillId(skillId);
            if (latest == null) {
                return List.of();
            }
            return List.of(convertToVersionVO(latest));
        }
    }

    /**
     * 转换为 DetailVO
     */
    private SkillDetailVO convertToDetailVO(Skill skill) {
        SkillDetailVO vo = new SkillDetailVO();
        vo.setId(skill.getId());
        vo.setName(skill.getName());
        vo.setDescription(skill.getDescription());
        vo.setCategory(skill.getCategory());
        vo.setLatestVersionId(skill.getLatestVersionId());
        vo.setLatestVersion(skill.getLatestVersion());
        vo.setTotalVersions(skill.getTotalVersions());
        vo.setCreatorId(skill.getCreatorId());
        vo.setDeptId(skill.getDeptId());
        vo.setDownloadCount(skill.getDownloadCount());
        vo.setStatus(skill.getStatus());
        vo.setReviewTime(skill.getReviewTime());
        vo.setReviewComment(skill.getReviewComment());
        vo.setCreatedAt(skill.getCreatedAt());
        vo.setUpdatedAt(skill.getUpdatedAt());

        // 查询用户和部门名称
        SysUser user = userMapper.selectById(skill.getCreatorId());
        if (user != null) {
            vo.setCreatorName(user.getRealName());
        }

        // 查询最新版本的 ossUrl（列表页直接下载用）
        if (skill.getLatestVersionId() != null) {
            SkillVersion latestVersion = versionMapper.selectById(skill.getLatestVersionId());
            if (latestVersion != null) {
                vo.setOssUrl(latestVersion.getOssUrl());
            }
        }

        return vo;
    }

    /**
     * 转换为 VersionVO
     */
    private SkillVersionVO convertToVersionVO(SkillVersion version) {
        SkillVersionVO vo = new SkillVersionVO();
        vo.setId(version.getId());
        vo.setSkillId(version.getSkillId());
        vo.setVersion(version.getVersion());
        vo.setMajor(version.getMajor());
        vo.setMinor(version.getMinor());
        vo.setPatch(version.getPatch());
        vo.setOssUrl(version.getOssUrl());
        vo.setFileSize(version.getFileSize());
        vo.setFileSizeText(formatFileSize(version.getFileSize()));
        vo.setChangelog(version.getChangelog());
        vo.setCreatedBy(version.getCreatedBy());
        vo.setCreatedAt(version.getCreatedAt());

        // 查询发布者名称
        SysUser user = userMapper.selectById(version.getCreatedBy());
        if (user != null) {
            vo.setCreatorName(user.getRealName());
        }

        return vo;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size == null || size <= 0) {
            return "0 B";
        }
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double value = size;
        while (value >= 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }
        return String.format("%.1f %s", value, units[unitIndex]);
    }

    /**
     * 检查技能是否存在
     */
    private void checkSkillExists(Skill skill) {
        if (skill == null || skill.getIsDeleted() == 1) {
            throw new BusinessException("技能不存在");
        }
    }

    /**
     * 检查编辑权限
     */
    private void checkCanEdit(Skill skill, Long userId, List<String> roles) {
        if (!canEdit(skill, userId, roles)) {
            throw new BusinessException("无权编辑此技能");
        }
    }

    /**
     * 检查删除权限
     */
    private void checkCanDelete(Skill skill, Long userId, List<String> roles) {
        if (!canDelete(skill, userId, roles)) {
            throw new BusinessException("无权删除此技能");
        }
    }

    private boolean isSuperAdmin(List<String> roles) {
        return roles != null && roles.contains("SUPER_ADMIN");
    }

    private boolean isAdmin(List<String> roles) {
        return roles != null && (roles.contains("OP_ADMIN") || roles.contains("SUPER_ADMIN"));
    }

    // ========== 兼容旧接口（已废弃） ==========

    @Override
    @Deprecated
    public PageResult<Skill> listSkills(int page, int size, String keyword, String category, Integer status) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Skill::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Skill::getName, keyword);
        }
        wrapper.orderByDesc(Skill::getCreatedAt);
        Page<Skill> p = skillMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    @Deprecated
    public Skill getSkillById(Long id) {
        Skill skill = skillMapper.selectById(id);
        if (skill == null || skill.getIsDeleted() == 1) {
            throw new BusinessException("技能不存在");
        }
        return skill;
    }
}

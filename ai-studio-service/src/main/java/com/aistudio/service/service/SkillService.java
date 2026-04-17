package com.aistudio.service.service;

import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.SkillCreateRequest;
import com.aistudio.service.dto.request.SkillUpdateRequest;
import com.aistudio.service.dto.request.SkillVersionRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.SkillDetailVO;
import com.aistudio.service.dto.response.SkillVersionVO;
import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SkillVersion;

import java.util.List;

/**
 * Skill 服务接口（OSS + 版本控制模式）
 */
public interface SkillService {

    /**
     * 创建技能
     */
    Long createSkill(SkillCreateRequest request, Long userId, Long deptId);

    /**
     * 发布新版本
     */
    String publishVersion(Long skillId, SkillVersionRequest request, Long userId, Long deptId, List<String> roles);

    /**
     * 更新技能基础信息（非版本）
     */
    void updateSkill(Long skillId, SkillUpdateRequest request, Long userId, List<String> roles);

    /**
     * 删除技能（软删除）
     */
    void deleteSkill(Long skillId, Long userId, List<String> roles);

    /**
     * 删除版本（物理删除，仅超管）
     */
    void deleteVersion(Long skillId, String version, Long userId, List<String> roles);

    /**
     * 审核技能
     */
    void auditSkill(Long skillId, AuditRequest request, Long userId, List<String> roles);

    /**
     * 获取技能详情
     */
    SkillDetailVO getSkillDetail(Long skillId, Long userId, List<String> roles);

    /**
     * 获取技能版本列表
     */
    List<SkillVersionVO> getSkillVersions(Long skillId, Long userId, List<String> roles);

    /**
     * 获取技能版本详情
     */
    SkillVersion getSkillVersion(Long skillId, String version);

    /**
     * 获取技能列表
     */
    PageResult<SkillDetailVO> listSkills(com.aistudio.service.dto.request.SkillQuery query,
                                          Long userId, Long deptId, List<String> roles);

    /**
     * 下载技能 ZIP 包（返回 OSS 预签名 URL）
     */
    String downloadSkill(Long skillId, String version, Long userId, List<String> roles);

    /**
     * 检查是否有编辑权限
     */
    boolean canEdit(Skill skill, Long userId, List<String> roles);

    /**
     * 检查是否有删除权限
     */
    boolean canDelete(Skill skill, Long userId, List<String> roles);

    /**
     * 检查是否可以查看所有版本
     */
    boolean canViewAllVersions(Long userId, List<String> roles);

    // ========== 以下方法已废弃（保留用于兼容性） ==========

    /**
     * @deprecated 使用新的 listSkills 方法
     */
    @Deprecated
    PageResult<Skill> listSkills(int page, int size, String keyword, String category, Integer status);

    /**
     * @deprecated 使用新的 getSkillDetail 方法
     */
    @Deprecated
    Skill getSkillById(Long id);
}

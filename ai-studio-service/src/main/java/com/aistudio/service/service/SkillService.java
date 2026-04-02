package com.aistudio.service.service;

import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SkillSyncLog;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SkillService {
    PageResult<Skill> listSkills(int page, int size, String keyword, String category, Integer status);

    Skill getSkillById(Long id);

    /**
     * 下载 Skill 文件内容
     * @param id Skill ID
     * @return 文件内容字符串
     */
    String downloadSkill(Long id);

    /**
     * 下载 Skill 作为 ZIP 包（包含 SKILL.md 和辅助文件）
     * @param id Skill ID
     * @return ZIP 文件字节数组
     */
    ResponseEntity<byte[]> downloadSkillAsZip(Long id);

    /**
     * 获取 Skill Raw 内容（供 Claude Code 使用）
     * @param id Skill ID
     * @return 文件内容字符串
     */
    String getSkillRawContent(Long id);

    /**
     * 获取同步日志
     * @param limit 数量限制
     * @return 同步日志列表
     */
    List<SkillSyncLog> getSyncLogs(int limit);
}

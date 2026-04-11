package com.aistudio.service.service.impl;

import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SkillSyncLog;
import com.aistudio.service.gitlab.GitLabClient;
import com.aistudio.service.gitlab.GitLabFile;
import com.aistudio.service.gitlab.GitLabTreeItem;
import com.aistudio.service.mapper.SkillMapper;
import com.aistudio.service.mapper.SkillSyncLogMapper;
import com.aistudio.service.service.SkillSyncService;
import com.aistudio.service.util.YamlFrontmatterParser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aistudio.service.gitlab.GitLabProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillSyncServiceImpl implements SkillSyncService {

    private final GitLabClient gitLabClient;
    private final SkillMapper skillMapper;
    private final SkillSyncLogMapper syncLogMapper;
    private final GitLabProperties gitLabProperties;
    private final YamlFrontmatterParser yamlParser = new YamlFrontmatterParser();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncResult syncAll() {
        long startTime = System.currentTimeMillis();
        int created = 0, updated = 0, deleted = 0, failed = 0;

        log.info("开始执行 Skill 全量同步...");

        try {
            // 1. 获取 Skills/ 目录下的所有直接子文件夹（每个文件夹是一个技能）
            List<GitLabTreeItem> treeItems = gitLabClient.getRepositoryTree(gitLabProperties.getBasePath(), false);
            Set<String> currentSkillPaths = new HashSet<>();

            // 2. 遍历每个技能文件夹
            for (GitLabTreeItem item : treeItems) {
                // 只处理直接子文件夹
                if (!"tree".equals(item.getType())) {
                    continue;
                }

                String skillFolder = item.getPath(); // e.g., Skills/xlsx

                // 获取该文件夹下的所有文件
                List<GitLabTreeItem> skillFiles = gitLabClient.getRepositoryTree(skillFolder, false);
                GitLabFile skillFile = null;
                String skillMdPath = null;

                // 查找 SKILL.md（全大写）
                boolean foundSkillMd = false;
                for (GitLabTreeItem fileItem : skillFiles) {
                    if ("blob".equals(fileItem.getType()) && "SKILL.md".equals(fileItem.getName())) {
                        foundSkillMd = true;
                        break;
                    }
                }

                // 如果找不到 SKILL.md，跳过该文件夹
                if (!foundSkillMd) {
                    log.warn("技能文件夹 {} 下未找到 SKILL.md，已跳过", skillFolder);
                    continue;
                }

                // 使用确定的路径获取文件
                skillMdPath = skillFolder + "/SKILL.md";
                skillFile = gitLabClient.getFile(skillMdPath);

                if (skillFile == null) {
                    log.warn("无法读取技能文件: {}，已跳过", skillMdPath);
                    failed++;
                    continue;
                }

                currentSkillPaths.add(skillMdPath);

                try {
                    // 同步该技能
                    syncSingleSkill(skillMdPath, skillFile);
                    // 判断是新增还是更新
                    Skill existing = skillMapper.selectByGitlabPath(skillMdPath);
                    if (existing == null) {
                        created++;
                    } else {
                        updated++;
                    }
                } catch (Exception e) {
                    log.error("同步技能失败: {}", skillFolder, e);
                    failed++;
                }
            }

            // 3. 标记已删除的技能（数据库中存在但 GitLab 中已不存在）
            List<Skill> allSkills = skillMapper.selectAllActive();
            for (Skill skill : allSkills) {
                if (!currentSkillPaths.contains(skill.getGitlabPath())) {
                    skill.setIsDeleted(1);
                    skill.setSyncStatus(1);
                    skill.setLastSyncAt(LocalDateTime.now());
                    skillMapper.updateById(skill);
                    deleted++;
                    log.info("标记删除 Skill: {}", skill.getName());
                }
            }

            long durationMs = System.currentTimeMillis() - startTime;
            log.info("同步完成: 新增 {}, 更新 {}, 删除 {}, 失败 {}, 耗时 {}ms",
                    created, updated, deleted, failed, durationMs);

            // 4. 记录同步日志
            saveSyncLog(created, updated, deleted, failed, durationMs, "manual", null);

            return new SyncResult(created, updated, deleted, failed, durationMs);

        } catch (Exception e) {
            log.error("同步过程发生异常", e);
            saveSyncLog(created, updated, deleted, failed,
                    System.currentTimeMillis() - startTime, "manual", e.getMessage());
            throw new RuntimeException("同步失败: " + e.getMessage(), e);
        }
    }

    /**
     * 同步单个技能（Skill.md 文件）
     */
    private void syncSingleSkill(String skillMdPath, GitLabFile file) {
        if (file == null || file.getContentPlain() == null) {
            throw new RuntimeException("无法获取 SKILL.md 文件内容: " + skillMdPath);
        }

        // 解析 YAML frontmatter
        YamlFrontmatterParser.ParseResult parseResult = yamlParser.parse(file.getContentPlain());
        Map<String, Object> frontmatter = parseResult.getFrontmatter();

        // 提取技能文件夹名称作为默认名称
        String skillFolderName = extractSkillNameFromPath(skillMdPath);

        // 提取元数据（顶层字段），name 默认为文件夹名
        String name = YamlFrontmatterParser.getString(frontmatter, "name", skillFolderName);
        String description = YamlFrontmatterParser.getString(frontmatter, "description", "");
        String category = YamlFrontmatterParser.getString(frontmatter, "category", "");
        String source = YamlFrontmatterParser.getString(frontmatter, "source", "gitlab");

        // 处理 metadata 对象
        String author = "";
        String tags = "";

        if (frontmatter.containsKey("metadata")) {
            Object metadataObj = frontmatter.get("metadata");
            if (metadataObj instanceof Map<?, ?> metadataMap) {
                // 从 metadata 对象中提取字段
                author = getMetadataValue(metadataMap, "author", "");
                // metadata.source 覆盖顶层 source
                String metadataSource = getMetadataValue(metadataMap, "source", "");
                if (!metadataSource.isEmpty()) {
                    source = metadataSource;
                }
                tags = extractTags(metadataMap.get("tags"));
            }
        }

        // 如果 metadata 中没有，尝试从顶层获取
        if (author.isEmpty()) {
            author = YamlFrontmatterParser.getString(frontmatter, "author", "");
        }
        if (tags.isEmpty()) {
            tags = extractTags(frontmatter.get("tags"));
        }

        // 查询或创建 Skill
        Skill skill = skillMapper.selectByGitlabPath(skillMdPath);
        if (skill == null) {
            skill = new Skill();
            skill.setGitlabPath(skillMdPath);
            skill.setDownloadCount(0);
            skill.setCreatedBy(1L); // 系统创建
        }

        // 更新字段
        skill.setName(name);
        skill.setDescription(description);
        skill.setCategory(category);
        skill.setSource(source);
        skill.setAuthor(author);
        skill.setTags(tags);
        skill.setGitlabCommitSha(file.getCommitId());
        skill.setSyncStatus(1); // 成功
        skill.setLastSyncAt(LocalDateTime.now());
        skill.setSyncError(null);
        skill.setIsDeleted(0);

        // 保存
        if (skill.getId() == null) {
            skillMapper.insert(skill);
        } else {
            skillMapper.updateById(skill);
        }

        log.debug("同步成功: {} -> {}", skillMdPath, name);
    }

    /**
     * 从 metadata Map 中获取字符串值
     */
    private String getMetadataValue(Map<?, ?> metadata, String key, String defaultValue) {
        if (metadata == null || !metadata.containsKey(key)) {
            return defaultValue;
        }
        Object value = metadata.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * 提取 tags，支持数组和字符串格式
     */
    private String extractTags(Object tagsObj) {
        if (tagsObj == null) {
            return "";
        }
        if (tagsObj instanceof List<?> tagList) {
            return tagList.stream()
                    .filter(item -> item != null)
                    .map(Object::toString)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));
        }
        return tagsObj.toString();
    }

    /**
     * 从 Skill.md 路径提取技能名称
     * 规则：Skills/{skill-name}/Skill.md → skill-name
     */
    private String extractSkillNameFromPath(String path) {
        // 移除 basePath 前缀（如 Skills/）
        String basePath = gitLabProperties.getBasePath();
        String relativePath = path.startsWith(basePath) ? path.substring(basePath.length()) : path;

        // 移除开头的 /
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // Skills/algorithmic-art/Skill.md → algorithmic-art
        String[] parts = relativePath.split("/");
        if (parts.length >= 1) {
            return parts[0];
        }
        return relativePath;
    }

    /**
     * 保存同步日志
     */
    private void saveSyncLog(int created, int updated, int deleted, int failed,
                             long durationMs, String triggerType, String errorMessage) {
        SkillSyncLog log = new SkillSyncLog();
        log.setCreatedCount(created);
        log.setUpdatedCount(updated);
        log.setDeletedCount(deleted);
        log.setErrorCount(failed);
        log.setDurationMs((int) durationMs);
        log.setTriggerType(triggerType);
        log.setErrorMessage(errorMessage);
        log.setCreatedAt(LocalDateTime.now());
        syncLogMapper.insert(log);
    }
}

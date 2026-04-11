package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SkillSyncLog;
import com.aistudio.service.gitlab.GitLabClient;
import com.aistudio.service.gitlab.GitLabFile;
import com.aistudio.service.gitlab.GitLabTreeItem;
import com.aistudio.service.mapper.SkillMapper;
import com.aistudio.service.mapper.SkillSyncLogMapper;
import com.aistudio.service.service.SkillService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillMapper skillMapper;
    private final SkillSyncLogMapper syncLogMapper;
    private final GitLabClient gitLabClient;

    @Override
    public PageResult<Skill> listSkills(int page, int size, String keyword, String category, Integer status) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        // 只查询未删除的
        wrapper.eq(Skill::getIsDeleted, 0);

        if (StringUtils.hasText(keyword)) wrapper.like(Skill::getName, keyword);
        if (StringUtils.hasText(category)) wrapper.eq(Skill::getCategory, category);
        if (status != null) wrapper.eq(Skill::getSyncStatus, status);
        wrapper.orderByDesc(Skill::getCreatedAt);
        Page<Skill> p = skillMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    public Skill getSkillById(Long id) {
        Skill skill = skillMapper.selectById(id);
        if (skill == null) throw new BusinessException(404, "Skill 不存在");
        if (skill.getIsDeleted() == 1) throw new BusinessException(404, "Skill 已删除");
        return skill;
    }

    @Override
    public String downloadSkill(Long id) {
        Skill skill = getSkillById(id);
        // 从 GitLab 获取内容
        String content = gitLabClient.getRawContent(skill.getGitlabPath());
        if (content == null) {
            throw new BusinessException(503, "无法从 GitLab 获取文件内容");
        }
        // 增加下载次数
        skillMapper.incrementDownloadCount(id);
        return content;
    }

    @Override
    public ResponseEntity<byte[]> downloadSkillAsZip(Long id) {
        Skill skill = getSkillById(id);
        String gitlabPath = skill.getGitlabPath();

        // 提取目录路径（去掉文件名 SKILL.md）
        String directoryPath = "";
        int lastSlashIndex = gitlabPath.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            directoryPath = gitlabPath.substring(0, lastSlashIndex);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {

            // 递归获取目录下所有文件并打包
            packDirectoryToZip(zos, directoryPath, "");

            // 确保至少有一个条目被写入
            zos.finish();

            byte[] zipBytes = baos.toByteArray();

            if (zipBytes.length == 0) {
                throw new BusinessException(503, "ZIP 文件为空，目录可能不存在或无文件");
            }

            // 增加下载次数
            skillMapper.incrementDownloadCount(id);

            String zipFilename = skill.getName() + ".zip";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFilename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            throw new BusinessException(503, "生成 ZIP 文件失败: " + e.getMessage());
        }
    }

    /**
     * 递归将目录内容打包到 ZIP
     * @param zos ZIP 输出流
     * @param gitLabDirPath GitLab 中的目录路径
     * @param zipEntryPrefix ZIP 中的路径前缀
     */
    private void packDirectoryToZip(ZipOutputStream zos, String gitLabDirPath, String zipEntryPrefix) throws Exception {
        List<GitLabTreeItem> items = gitLabClient.getRepositoryTree(gitLabDirPath, false);

        if (items == null || items.isEmpty()) {
            return;
        }

        for (GitLabTreeItem item : items) {
            if ("blob".equals(item.getType())) {
                // 文件：获取二进制内容并写入 ZIP
                byte[] content = gitLabClient.getRawContentBytes(item.getPath());
                if (content != null) {
                    // 构建 ZIP 中的条目名称：前缀 + 文件名
                    String entryName = zipEntryPrefix + item.getName();
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    zos.write(content);
                    zos.closeEntry();
                }
            } else if ("tree".equals(item.getType())) {
                // 目录：递归处理，前缀加上目录名/
                String subDirPath = gitLabDirPath.isEmpty() ? item.getName() : gitLabDirPath + "/" + item.getName();
                String newPrefix = zipEntryPrefix + item.getName() + "/";
                packDirectoryToZip(zos, subDirPath, newPrefix);
            }
        }
    }

    @Override
    public String getSkillRawContent(Long id) {
        Skill skill = getSkillById(id);
        String content = gitLabClient.getRawContent(skill.getGitlabPath());
        if (content == null) {
            throw new BusinessException(503, "无法从 GitLab 获取文件内容");
        }
        // 增加下载次数
        skillMapper.incrementDownloadCount(id);
        return content;
    }

    @Override
    public List<SkillSyncLog> getSyncLogs(int limit) {
        LambdaQueryWrapper<SkillSyncLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SkillSyncLog::getCreatedAt);
        Page<SkillSyncLog> page = new Page<>(1, limit);
        return syncLogMapper.selectPage(page, wrapper).getRecords();
    }
}

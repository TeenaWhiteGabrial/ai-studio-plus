package com.aistudio.service.service.impl;

import com.aistudio.service.entity.GitlabActivityLink;
import com.aistudio.service.mapper.GitlabActivityLinkMapper;
import com.aistudio.service.service.GitlabActivityLinkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitlabActivityLinkServiceImpl implements GitlabActivityLinkService {

    private final GitlabActivityLinkMapper linkMapper;

    @Override
    public void link(Long eventLogId, String targetType, Long targetId, String relationType) {
        if (eventLogId == null || targetId == null || blank(targetType) || blank(relationType)) {
            return;
        }
        GitlabActivityLink existing = linkMapper.selectOne(new LambdaQueryWrapper<GitlabActivityLink>()
                .eq(GitlabActivityLink::getEventLogId, eventLogId)
                .eq(GitlabActivityLink::getTargetType, targetType)
                .eq(GitlabActivityLink::getTargetId, targetId)
                .eq(GitlabActivityLink::getRelationType, relationType)
                .last("LIMIT 1"));
        if (existing != null) {
            return;
        }
        GitlabActivityLink link = new GitlabActivityLink();
        link.setEventLogId(eventLogId);
        link.setTargetType(targetType);
        link.setTargetId(targetId);
        link.setRelationType(relationType);
        linkMapper.insert(link);
    }

    @Override
    public List<GitlabActivityLink> listByEventLogId(Long eventLogId) {
        return linkMapper.selectList(new LambdaQueryWrapper<GitlabActivityLink>()
                .eq(GitlabActivityLink::getEventLogId, eventLogId)
                .orderByAsc(GitlabActivityLink::getId));
    }

    @Override
    public List<GitlabActivityLink> listByTarget(String targetType, Long targetId) {
        return linkMapper.selectList(new LambdaQueryWrapper<GitlabActivityLink>()
                .eq(GitlabActivityLink::getTargetType, targetType)
                .eq(GitlabActivityLink::getTargetId, targetId)
                .orderByAsc(GitlabActivityLink::getId));
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}

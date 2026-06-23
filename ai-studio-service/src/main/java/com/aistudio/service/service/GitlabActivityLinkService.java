package com.aistudio.service.service;

import com.aistudio.service.entity.GitlabActivityLink;

import java.util.List;

public interface GitlabActivityLinkService {
    void link(Long eventLogId, String targetType, Long targetId, String relationType);
    List<GitlabActivityLink> listByEventLogId(Long eventLogId);
    List<GitlabActivityLink> listByTarget(String targetType, Long targetId);
}

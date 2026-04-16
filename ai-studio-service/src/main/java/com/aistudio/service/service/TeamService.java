package com.aistudio.service.service;

import com.aistudio.service.dto.request.TeamCreateRequest;
import com.aistudio.service.dto.request.TeamUpdateRequest;
import com.aistudio.service.entity.SysUser;

import java.util.List;

public interface TeamService {

    List<Object> listTeams(Long deptId);

    Long createTeam(TeamCreateRequest request);

    void updateTeam(Long id, TeamUpdateRequest request);

    void deleteTeam(Long id);

    List<SysUser> getTeamMembers(Long teamId);

    void addTeamMembers(Long teamId, List<Long> userIds);

    void removeTeamMember(Long teamId, Long userId);
}

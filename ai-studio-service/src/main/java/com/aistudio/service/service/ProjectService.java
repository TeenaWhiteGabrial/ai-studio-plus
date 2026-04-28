package com.aistudio.service.service;

import com.aistudio.service.dto.request.ProjectCreateRequest;
import com.aistudio.service.dto.request.ProjectUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.ProjectVO;

import java.util.List;

public interface ProjectService {

    PageResult<ProjectVO> listProjects(int page, int size, String keyword, String status, Long ownerId);

    List<ProjectVO> listActiveProjects();

    List<ProjectVO> listActiveProjectsForCurrentUserTeam();

    ProjectVO getProject(Long id);

    Long createProject(ProjectCreateRequest request);

    void updateProject(Long id, ProjectUpdateRequest request);

    void deleteProject(Long id);

    void updateProjectStatus(Long id, String status);
}

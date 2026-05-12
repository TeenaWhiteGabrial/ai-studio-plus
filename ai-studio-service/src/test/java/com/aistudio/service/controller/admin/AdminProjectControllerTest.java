package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.ProjectCreateRequest;
import com.aistudio.service.dto.request.ProjectUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.ProjectVO;
import com.aistudio.service.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminProjectControllerTest {

    private final RecordingProjectService projectService = new RecordingProjectService();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminProjectController(projectService))
            .build();

    @Test
    void listUsesAdminProjectPath() throws Exception {
        mockMvc.perform(get("/admin/project/list")
                        .param("page", "1")
                        .param("size", "20")
                        .param("keyword", "studio")
                        .param("status", "ACTIVE")
                        .param("owner_id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));

        assertThat(projectService.lastPage).isEqualTo(1);
        assertThat(projectService.lastSize).isEqualTo(20);
        assertThat(projectService.lastKeyword).isEqualTo("studio");
        assertThat(projectService.lastStatus).isEqualTo("ACTIVE");
        assertThat(projectService.lastOwnerId).isEqualTo(7L);
    }

    @Test
    void createAcceptsSnakeCasePayload() throws Exception {
        mockMvc.perform(post("/admin/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "project_name": "AI Studio",
                                  "description": "Admin migration",
                                  "owner_id": 7,
                                  "team_id": 1,
                                  "dept_id": 1,
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(11));

        assertThat(projectService.lastCreateRequest.getProjectName()).isEqualTo("AI Studio");
        assertThat(projectService.lastCreateRequest.getOwnerId()).isEqualTo(7L);
    }

    private static class RecordingProjectService implements ProjectService {
        private int lastPage;
        private int lastSize;
        private String lastKeyword;
        private String lastStatus;
        private Long lastOwnerId;
        private ProjectCreateRequest lastCreateRequest;

        @Override
        public PageResult<ProjectVO> listProjects(int page, int size, String keyword, String status, Long ownerId) {
            this.lastPage = page;
            this.lastSize = size;
            this.lastKeyword = keyword;
            this.lastStatus = status;
            this.lastOwnerId = ownerId;
            return PageResult.of(0L, List.of());
        }

        @Override
        public List<ProjectVO> listActiveProjects() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<ProjectVO> listActiveProjectsForCurrentUserTeam() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProjectVO getProject(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long createProject(ProjectCreateRequest request) {
            this.lastCreateRequest = request;
            return 11L;
        }

        @Override
        public void updateProject(Long id, ProjectUpdateRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteProject(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateProjectStatus(Long id, String status) {
            throw new UnsupportedOperationException();
        }
    }
}

package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.ProjectCreateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminProjectControllerTest {

    private final ProjectService projectService = mock(ProjectService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminProjectController(projectService))
            .build();

    @Test
    void listUsesAdminProjectPath() throws Exception {
        when(projectService.listProjects(1, 20, "studio", "ACTIVE", 7L))
                .thenReturn(PageResult.of(0L, List.of()));

        mockMvc.perform(get("/admin/project/list")
                        .param("page", "1")
                        .param("size", "20")
                        .param("keyword", "studio")
                        .param("status", "ACTIVE")
                        .param("owner_id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));

        verify(projectService).listProjects(1, 20, "studio", "ACTIVE", 7L);
    }

    @Test
    void createAcceptsSnakeCasePayload() throws Exception {
        when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(11L);

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

        ArgumentCaptor<ProjectCreateRequest> captor = ArgumentCaptor.forClass(ProjectCreateRequest.class);
        verify(projectService).createProject(captor.capture());
        assertThat(captor.getValue().getProjectName()).isEqualTo("AI Studio");
        assertThat(captor.getValue().getOwnerId()).isEqualTo(7L);
    }
}

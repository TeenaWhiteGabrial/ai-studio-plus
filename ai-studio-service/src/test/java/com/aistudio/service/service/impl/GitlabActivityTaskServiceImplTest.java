package com.aistudio.service.service.impl;

import com.aistudio.service.entity.ActivityTaskExecution;
import com.aistudio.service.mapper.ActivityTaskExecutionMapper;
import com.aistudio.service.mapper.GitlabEventLogMapper;
import com.aistudio.service.mapper.GitlabProjectConfigMapper;
import com.aistudio.service.service.GitlabActivityAnalysisService;
import com.aistudio.service.service.GitlabActivityBackfillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitlabActivityTaskServiceImplTest {

    @Mock
    private GitlabEventLogMapper eventLogMapper;
    @Mock
    private ActivityTaskExecutionMapper taskExecutionMapper;
    @Mock
    private GitlabActivityAnalysisService analysisService;
    @Mock
    private GitlabActivityBackfillService backfillService;
    @Mock
    private GitlabProjectConfigMapper projectConfigMapper;

    private GitlabActivityTaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new GitlabActivityTaskServiceImpl(
                eventLogMapper,
                taskExecutionMapper,
                analysisService,
                backfillService,
                projectConfigMapper,
                new ObjectMapper().findAndRegisterModules()
        );
    }

    @Test
    void processPendingTasksRoutesAnalyzeTaskUsingPayloadGitlabProjectId() {
        ActivityTaskExecution task = new ActivityTaskExecution();
        task.setId(1L);
        task.setTaskType(GitlabActivityTaskServiceImpl.TASK_ANALYZE);
        task.setTargetDate(LocalDate.of(2026, 6, 22));
        task.setStatus("PENDING");
        task.setAttemptCount(0);
        task.setNextRunAt(LocalDateTime.now().minusMinutes(1));
        task.setPayloadJson("""
                {"gitlab_project_id":101,"project_id":11,"event_log_id":55}
                """);
        when(taskExecutionMapper.selectList(any())).thenReturn(List.of(task));

        taskService.processPendingTasks();

        verify(analysisService).analyzeUserProject(null, 101L, LocalDate.of(2026, 6, 22), 55L);
        ArgumentCaptor<ActivityTaskExecution> captor = ArgumentCaptor.forClass(ActivityTaskExecution.class);
        verify(taskExecutionMapper, org.mockito.Mockito.atLeast(2)).updateById(captor.capture());
        assertThat(captor.getAllValues().get(captor.getAllValues().size() - 1).getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void processPendingTasksRoutesBackfillTaskUsingPayloadGitlabProjectId() {
        ActivityTaskExecution task = new ActivityTaskExecution();
        task.setId(2L);
        task.setTaskType(GitlabActivityTaskServiceImpl.TASK_BACKFILL);
        task.setTargetDate(LocalDate.of(2026, 6, 21));
        task.setStatus("PENDING");
        task.setAttemptCount(0);
        task.setNextRunAt(LocalDateTime.now().minusMinutes(1));
        task.setPayloadJson("""
                {"gitlab_project_id":202,"project_id":22}
                """);
        when(taskExecutionMapper.selectList(any())).thenReturn(List.of(task));
        doNothing().when(backfillService).backfillProject(202L, LocalDate.of(2026, 6, 21));

        taskService.processPendingTasks();

        verify(backfillService).backfillProject(202L, LocalDate.of(2026, 6, 21));
    }

    @Test
    void retryTaskResetsFailedTaskToPending() {
        ActivityTaskExecution task = new ActivityTaskExecution();
        task.setId(3L);
        task.setStatus("FAILED");
        task.setAttemptCount(3);
        task.setErrorMessage("boom");
        when(taskExecutionMapper.selectById(3L)).thenReturn(task);

        taskService.retryTask(3L);

        ArgumentCaptor<ActivityTaskExecution> captor = ArgumentCaptor.forClass(ActivityTaskExecution.class);
        verify(taskExecutionMapper).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
        assertThat(captor.getValue().getErrorMessage()).isNull();
        assertThat(captor.getValue().getNextRunAt()).isNotNull();
    }

    @Test
    void runTaskNowRejectsSuccessfulTask() {
        ActivityTaskExecution task = new ActivityTaskExecution();
        task.setId(4L);
        task.setStatus("SUCCESS");
        when(taskExecutionMapper.selectById(4L)).thenReturn(task);

        assertThatThrownBy(() -> taskService.runTaskNow(4L))
                .hasMessageContaining("成功任务无需重复执行");

        verify(analysisService, never()).analyzeUserProject(any(), any(), any());
        verify(backfillService, never()).backfillProject(any(), any());
    }
}

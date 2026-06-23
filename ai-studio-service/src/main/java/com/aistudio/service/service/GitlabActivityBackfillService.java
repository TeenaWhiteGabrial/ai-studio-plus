package com.aistudio.service.service;

import java.time.LocalDate;

public interface GitlabActivityBackfillService {
    void backfillProject(Long gitlabProjectId, LocalDate targetDate);
}

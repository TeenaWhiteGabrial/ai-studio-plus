CREATE TABLE IF NOT EXISTS gitlab_project_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    gitlab_project_id BIGINT NOT NULL,
    gitlab_project_name VARCHAR(255) NOT NULL,
    gitlab_project_path VARCHAR(255) DEFAULT NULL,
    gitlab_group_name VARCHAR(255) DEFAULT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_by BIGINT DEFAULT NULL,
    updated_by BIGINT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_project_config_gitlab_project_id (gitlab_project_id),
    KEY idx_gitlab_project_config_project_id (project_id)
);

CREATE TABLE IF NOT EXISTS activity_stat_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(128) NOT NULL,
    group_key VARCHAR(128) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    scope_type VARCHAR(32) NOT NULL DEFAULT 'TEAM',
    enabled TINYINT NOT NULL DEFAULT 1,
    created_by BIGINT DEFAULT NULL,
    updated_by BIGINT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_stat_group_group_key (group_key)
);

CREATE TABLE IF NOT EXISTS activity_stat_group_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_stat_group_member (stat_group_id, user_id),
    KEY idx_activity_stat_group_member_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS gitlab_user_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gitlab_user_id BIGINT NOT NULL,
    username VARCHAR(128) NOT NULL,
    name VARCHAR(255) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    state VARCHAR(64) DEFAULT NULL,
    synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_user_snapshot_gitlab_user_id (gitlab_user_id)
);

CREATE TABLE IF NOT EXISTS gitlab_project_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gitlab_project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    path_with_namespace VARCHAR(255) DEFAULT NULL,
    web_url VARCHAR(500) DEFAULT NULL,
    default_branch VARCHAR(128) DEFAULT NULL,
    synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_project_snapshot_gitlab_project_id (gitlab_project_id)
);

CREATE TABLE IF NOT EXISTS gitlab_member_fact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gitlab_project_id BIGINT NOT NULL,
    gitlab_user_id BIGINT NOT NULL,
    access_level INT DEFAULT NULL,
    member_state VARCHAR(64) DEFAULT NULL,
    synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_member_fact_project_user (gitlab_project_id, gitlab_user_id)
);

CREATE TABLE IF NOT EXISTS gitlab_branch_fact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gitlab_project_id BIGINT NOT NULL,
    branch_name VARCHAR(255) NOT NULL,
    is_default_branch TINYINT NOT NULL DEFAULT 0,
    last_commit_sha VARCHAR(64) DEFAULT NULL,
    synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_branch_fact_project_branch (gitlab_project_id, branch_name)
);

CREATE TABLE IF NOT EXISTS gitlab_mr_fact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gitlab_mr_id BIGINT NOT NULL,
    gitlab_project_id BIGINT NOT NULL,
    iid BIGINT DEFAULT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT NULL,
    state VARCHAR(64) DEFAULT NULL,
    source_branch VARCHAR(255) DEFAULT NULL,
    target_branch VARCHAR(255) DEFAULT NULL,
    author_gitlab_user_id BIGINT DEFAULT NULL,
    created_at_gitlab DATETIME DEFAULT NULL,
    updated_at_gitlab DATETIME DEFAULT NULL,
    last_synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_mr_fact_gitlab_mr_id (gitlab_mr_id),
    KEY idx_gitlab_mr_fact_project_id (gitlab_project_id)
);

CREATE TABLE IF NOT EXISTS gitlab_commit_fact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    gitlab_project_id BIGINT NOT NULL,
    commit_sha VARCHAR(64) NOT NULL,
    short_sha VARCHAR(16) DEFAULT NULL,
    author_gitlab_user_id BIGINT DEFAULT NULL,
    author_name VARCHAR(255) DEFAULT NULL,
    author_email VARCHAR(255) DEFAULT NULL,
    commit_message VARCHAR(1000) DEFAULT NULL,
    branch_name VARCHAR(255) DEFAULT NULL,
    committed_at DATETIME DEFAULT NULL,
    pushed_at DATETIME DEFAULT NULL,
    file_paths_json JSON DEFAULT NULL,
    diff_summary TEXT DEFAULT NULL,
    mr_ids_json JSON DEFAULT NULL,
    last_synced_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_commit_fact_project_sha (gitlab_project_id, commit_sha),
    KEY idx_gitlab_commit_fact_author_date (author_gitlab_user_id, committed_at)
);

CREATE TABLE IF NOT EXISTS gitlab_event_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_uid VARCHAR(128) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    project_id BIGINT DEFAULT NULL,
    gitlab_project_id BIGINT DEFAULT NULL,
    gitlab_user_id BIGINT DEFAULT NULL,
    ref_name VARCHAR(255) DEFAULT NULL,
    event_time DATETIME DEFAULT NULL,
    payload_json JSON NOT NULL,
    process_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    process_message VARCHAR(1000) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_event_log_event_uid (event_uid),
    KEY idx_gitlab_event_log_status_time (process_status, created_at)
);

CREATE TABLE IF NOT EXISTS activity_work_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    analysis_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    gitlab_user_id BIGINT DEFAULT NULL,
    project_id BIGINT DEFAULT NULL,
    gitlab_project_id BIGINT DEFAULT NULL,
    stat_group_id BIGINT DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT DEFAULT NULL,
    work_type VARCHAR(64) NOT NULL DEFAULT 'feature',
    progress_status VARCHAR(64) NOT NULL DEFAULT 'uncertain',
    risk_level VARCHAR(32) NOT NULL DEFAULT 'low',
    risk_summary VARCHAR(1000) DEFAULT NULL,
    blocking_status VARCHAR(32) NOT NULL DEFAULT 'normal',
    blocking_summary VARCHAR(1000) DEFAULT NULL,
    module_names VARCHAR(1000) DEFAULT NULL,
    related_commit_count INT NOT NULL DEFAULT 0,
    related_mr_count INT NOT NULL DEFAULT 0,
    confidence DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    evidence_summary TEXT DEFAULT NULL,
    analysis_engine VARCHAR(64) NOT NULL DEFAULT 'heuristic_v1',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_activity_work_item_user_date (user_id, analysis_date),
    KEY idx_activity_work_item_group_date (stat_group_id, analysis_date)
);

CREATE TABLE IF NOT EXISTS activity_work_item_evidence (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_item_id BIGINT NOT NULL,
    evidence_type VARCHAR(32) NOT NULL,
    evidence_ref VARCHAR(255) NOT NULL,
    summary VARCHAR(1000) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_activity_work_item_evidence_work_item_id (work_item_id)
);

CREATE TABLE IF NOT EXISTS activity_daily_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    analysis_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    gitlab_user_id BIGINT DEFAULT NULL,
    project_id BIGINT DEFAULT NULL,
    gitlab_project_id BIGINT DEFAULT NULL,
    stat_group_id BIGINT DEFAULT NULL,
    work_summary TEXT DEFAULT NULL,
    progress_summary VARCHAR(1000) DEFAULT NULL,
    risk_summary VARCHAR(1000) DEFAULT NULL,
    blocking_summary VARCHAR(1000) DEFAULT NULL,
    analysis_engine VARCHAR(64) NOT NULL DEFAULT 'heuristic_v1',
    status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_daily_analysis_user_project_date (analysis_date, user_id, gitlab_project_id)
);

CREATE TABLE IF NOT EXISTS activity_daily_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_date DATE NOT NULL,
    report_scope VARCHAR(32) NOT NULL,
    user_id BIGINT DEFAULT NULL,
    project_id BIGINT DEFAULT NULL,
    stat_group_id BIGINT DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    report_data_json JSON DEFAULT NULL,
    generated_by VARCHAR(64) NOT NULL DEFAULT 'heuristic_v1',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_daily_report_scope (report_date, report_scope, user_id, project_id, stat_group_id)
);

CREATE TABLE IF NOT EXISTS activity_task_execution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_type VARCHAR(64) NOT NULL,
    target_date DATE DEFAULT NULL,
    user_id BIGINT DEFAULT NULL,
    project_id BIGINT DEFAULT NULL,
    target_ref VARCHAR(255) DEFAULT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    attempt_count INT NOT NULL DEFAULT 0,
    next_run_at DATETIME DEFAULT NULL,
    error_message VARCHAR(1000) DEFAULT NULL,
    payload_json JSON DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_activity_task_execution_status_next_run (status, next_run_at)
);

CREATE TABLE IF NOT EXISTS gitlab_activity_link (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_log_id BIGINT NOT NULL,
    target_type VARCHAR(64) NOT NULL,
    target_id BIGINT NOT NULL,
    relation_type VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_gitlab_activity_link_unique (event_log_id, target_type, target_id, relation_type),
    KEY idx_gitlab_activity_link_target (target_type, target_id),
    KEY idx_gitlab_activity_link_event (event_log_id)
);

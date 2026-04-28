ALTER TABLE `daily_task`
    ADD COLUMN `ai_participation` INT NOT NULL DEFAULT 0 COMMENT 'AI 参与度：0/25/50/75/100' AFTER `hours`,
    ADD COLUMN `output_id` BIGINT DEFAULT NULL COMMENT '关联产出统计ID' AFTER `ai_participation`,
    ADD INDEX `idx_output_id` (`output_id`);

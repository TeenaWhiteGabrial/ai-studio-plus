ALTER TABLE `project`
    ADD COLUMN `dept_id` BIGINT NULL COMMENT '所属部门ID' AFTER `owner_id`,
    ADD COLUMN `team_id` BIGINT NULL COMMENT '所属团队ID' AFTER `dept_id`;

UPDATE `project` p
    INNER JOIN `sys_user` u ON p.`owner_id` = u.`id`
SET p.`dept_id` = u.`dept_id`,
    p.`team_id` = u.`team_id`
WHERE p.`dept_id` IS NULL
   OR p.`team_id` IS NULL;

CREATE INDEX `idx_project_dept_id` ON `project` (`dept_id`);
CREATE INDEX `idx_project_team_id` ON `project` (`team_id`);

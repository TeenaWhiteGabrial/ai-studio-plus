CREATE TABLE IF NOT EXISTS `article_folder` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章文件夹ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父文件夹ID',
    `folder_name` VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章文件夹表';

ALTER TABLE `article`
    ADD COLUMN `folder_id` BIGINT NULL COMMENT '所属文件夹ID' AFTER `author_name`;

CREATE INDEX `idx_article_folder_id` ON `article` (`folder_id`);

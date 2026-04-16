-- ========================================================
-- V11: Portal 技术社区表（文章、问答、评论、收藏扩展）
-- ========================================================

-- -------------------- 文章表 --------------------
CREATE TABLE IF NOT EXISTS `article` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `content` LONGTEXT NOT NULL COMMENT '文章内容(富文本HTML)',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片OSS Key',
    `author_id` BIGINT NOT NULL COMMENT '作者用户ID',
    `author_name` VARCHAR(100) NOT NULL COMMENT '作者姓名(冗余)',
    `views_count` INT DEFAULT 0 COMMENT '阅读数',
    `likes_count` INT DEFAULT 0 COMMENT '点赞数',
    `comments_count` INT DEFAULT 0 COMMENT '评论数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
    `followers_count` INT DEFAULT 0 COMMENT '粉丝数',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布 2-已下架',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`),
    FULLTEXT INDEX `ft_title_content` (`title`, `summary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- -------------------- 文章标签关联表 --------------------
CREATE TABLE IF NOT EXISTS `article_tag` (
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`article_id`, `tag_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- -------------------- 文章点赞表 --------------------
CREATE TABLE IF NOT EXISTS `article_like` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    INDEX `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞表';

-- -------------------- 问答表 --------------------
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '问题ID',
    `title` VARCHAR(200) NOT NULL COMMENT '问题标题',
    `content` LONGTEXT NOT NULL COMMENT '问题详情(富文本HTML)',
    `author_id` BIGINT NOT NULL COMMENT '提问用户ID',
    `author_name` VARCHAR(100) NOT NULL COMMENT '提问用户姓名(冗余)',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔',
    `answers_count` INT DEFAULT 0 COMMENT '回答数',
    `views_count` INT DEFAULT 0 COMMENT '浏览数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
    `followers_count` INT DEFAULT 0 COMMENT '粉丝数',
    `has_best_answer` TINYINT DEFAULT 0 COMMENT '是否有最佳答案: 0-无 1-有',
    `best_answer_id` BIGINT DEFAULT NULL COMMENT '最佳答案ID',
    `taken_down` TINYINT DEFAULT 0 COMMENT '是否下架: 0-正常 1-已下架',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_taken_down` (`taken_down`),
    INDEX `idx_created_at` (`created_at`),
    FULLTEXT INDEX `ft_title_content` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问答表';

-- -------------------- 问题标签关联表 --------------------
CREATE TABLE IF NOT EXISTS `question_tag` (
    `question_id` BIGINT NOT NULL COMMENT '问题ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`question_id`, `tag_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题标签关联表';

-- -------------------- 回答表 --------------------
CREATE TABLE IF NOT EXISTS `answer` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '回答ID',
    `question_id` BIGINT NOT NULL COMMENT '所属问题ID',
    `author_id` BIGINT NOT NULL COMMENT '回答用户ID',
    `author_name` VARCHAR(100) NOT NULL COMMENT '回答用户姓名(冗余)',
    `content` LONGTEXT NOT NULL COMMENT '回答内容(富文本HTML)',
    `likes_count` INT DEFAULT 0 COMMENT '点赞数',
    `is_best` TINYINT DEFAULT 0 COMMENT '是否最佳答案: 0-否 1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    INDEX `idx_question_id` (`question_id`),
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_is_best` (`is_best`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- -------------------- 回答点赞表 --------------------
CREATE TABLE IF NOT EXISTS `answer_like` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `answer_id` BIGINT NOT NULL COMMENT '回答ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY `uk_user_answer` (`user_id`, `answer_id`),
    INDEX `idx_answer_id` (`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答点赞表';

-- -------------------- 评论表（文章/问题/回答下的评论） --------------------
CREATE TABLE IF NOT EXISTS `community_comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    `comment_type` VARCHAR(20) NOT NULL COMMENT '评论类型: article/question/answer',
    `target_id` BIGINT NOT NULL COMMENT '被评论对象ID(文章ID/问题ID/回答ID)',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID(二级回复)',
    `root_id` BIGINT DEFAULT NULL COMMENT '根评论ID(用于二级嵌套)',
    `author_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `author_name` VARCHAR(100) NOT NULL COMMENT '评论用户姓名(冗余)',
    `author_avatar` VARCHAR(500) DEFAULT NULL COMMENT '评论用户头像',
    `content` VARCHAR(2000) NOT NULL COMMENT '评论内容',
    `likes_count` INT DEFAULT 0 COMMENT '点赞数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    INDEX `idx_target` (`comment_type`, `target_id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_root_id` (`root_id`),
    INDEX `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论表(文章/问答/回答)';

-- -------------------- 评论点赞表 --------------------
CREATE TABLE IF NOT EXISTS `community_comment_like` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `comment_id` BIGINT NOT NULL COMMENT '评论ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
    INDEX `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区评论点赞表';

-- -------------------- 用户关注表 --------------------
CREATE TABLE IF NOT EXISTS `user_follow` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关注ID',
    `follower_id` BIGINT NOT NULL COMMENT '关注者用户ID',
    `following_id` BIGINT NOT NULL COMMENT '被关注者用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
    INDEX `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- -------------------- 标签表 --------------------
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `type` VARCHAR(20) DEFAULT NULL COMMENT '标签类型: article/question(可为空)',
    `use_count` INT DEFAULT 0 COMMENT '使用次数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_name_type` (`name`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- -------------------- 浏览历史表 --------------------
CREATE TABLE IF NOT EXISTS `browse_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '浏览记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_type` VARCHAR(20) NOT NULL COMMENT '浏览对象类型: skill/plugin/article/question',
    `target_id` BIGINT NOT NULL COMMENT '浏览对象ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- -------------------- 通知表 --------------------
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '通知所属用户ID',
    `type` VARCHAR(30) NOT NULL COMMENT '通知类型: comment/reply/accept_like',
    `content` VARCHAR(500) NOT NULL COMMENT '通知内容摘要',
    `source_id` BIGINT DEFAULT NULL COMMENT '来源对象ID(文章/问题/评论ID)',
    `source_type` VARCHAR(20) DEFAULT NULL COMMENT '来源对象类型: article/question/comment',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- -------------------- 收藏表扩展 --------------------
-- 现有 favorite 表(resource_type: SKILL/MCP/PLUGIN...) 扩展支持 ARTICLE/QUESTION
-- 无需 DDL 变更，仅记录此扩展
-- 查询示例: SELECT * FROM favorite WHERE user_id=? AND resource_type IN ('ARTICLE','QUESTION')

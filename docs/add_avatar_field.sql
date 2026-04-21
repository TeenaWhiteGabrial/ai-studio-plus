-- 给 sys_user 表添加 avatar 字段
-- 如果字段不存在则添加
ALTER TABLE sys_user
ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) COMMENT '头像URL'
AFTER email;

-- 说明：
-- 此SQL脚本用于添加用户头像字段
-- 在生产环境执行前，请先备份数据库
-- 支持MySQL 8.0+和PostgreSQL 9.6+等现代数据库

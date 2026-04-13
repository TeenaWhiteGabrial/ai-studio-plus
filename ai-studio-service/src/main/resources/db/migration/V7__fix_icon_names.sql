-- ========================================================
-- V7: 修复图标名称（Element Plus icons-vue 中不存在以下图标）
--   Users -> Avatar
--   PuzzlePiece -> Operation
--   Projects -> PieChart
--   Tree -> List
-- ========================================================

UPDATE sys_menu SET icon = 'Avatar' WHERE icon = 'Users';
UPDATE sys_menu SET icon = 'Operation' WHERE icon = 'PuzzlePiece';
UPDATE sys_menu SET icon = 'PieChart' WHERE icon = 'Projects';
UPDATE sys_menu SET icon = 'List' WHERE icon = 'Tree';

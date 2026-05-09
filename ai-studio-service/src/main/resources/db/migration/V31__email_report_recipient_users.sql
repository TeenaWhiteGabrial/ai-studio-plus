SET @to_col_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'email_report_rule'
      AND column_name = 'to_recipient_user_ids_json'
);
SET @to_sql := IF(
    @to_col_exists = 0,
    'ALTER TABLE `email_report_rule` ADD COLUMN `to_recipient_user_ids_json` TEXT NULL COMMENT ''主送接收人用户ID JSON数组'' AFTER `name`',
    'SELECT ''to_recipient_user_ids_json already exists'''
);
PREPARE to_stmt FROM @to_sql;
EXECUTE to_stmt;
DEALLOCATE PREPARE to_stmt;

SET @cc_col_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'email_report_rule'
      AND column_name = 'cc_recipient_user_ids_json'
);
SET @cc_sql := IF(
    @cc_col_exists = 0,
    'ALTER TABLE `email_report_rule` ADD COLUMN `cc_recipient_user_ids_json` TEXT NULL COMMENT ''抄送接收人用户ID JSON数组'' AFTER `to_recipient_user_ids_json`',
    'SELECT ''cc_recipient_user_ids_json already exists'''
);
PREPARE cc_stmt FROM @cc_sql;
EXECUTE cc_stmt;
DEALLOCATE PREPARE cc_stmt;

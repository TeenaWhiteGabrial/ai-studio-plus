ALTER TABLE activity_daily_analysis
    ADD COLUMN report_title VARCHAR(255) DEFAULT NULL AFTER blocking_summary,
    ADD COLUMN report_content TEXT DEFAULT NULL AFTER report_title;

package com.aistudio.service.service.impl;

import com.aistudio.service.mapper.*;
import com.aistudio.service.service.DashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SkillMapper skillMapper;
    private final McpServerMapper mcpServerMapper;
    private final PluginMapper pluginMapper;
    private final TutorialMapper tutorialMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("skillCount", skillMapper.selectCount(null));
        result.put("mcpCount", mcpServerMapper.selectCount(null));
        result.put("pluginCount", pluginMapper.selectCount(null));
        result.put("tutorialCount", tutorialMapper.selectCount(null));

        Long skillDownloads = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(download_count),0) FROM skill", Long.class);
        Long pluginDownloads = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(download_count),0) FROM plugin", Long.class);
        Long tutorialViews = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(view_count),0) FROM tutorial", Long.class);
        result.put("skillDownloads", skillDownloads);
        result.put("pluginDownloads", pluginDownloads);
        result.put("tutorialViews", tutorialViews);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTrend(String granularity, LocalDate startDate, LocalDate endDate) {
        String groupExpr = switch (granularity) {
            case "week" -> "YEARWEEK(stat_date, 1)";
            case "month" -> "DATE_FORMAT(stat_date, '%Y-%m')";
            default -> "stat_date";
        };
        String sql = "SELECT " + groupExpr + " as period, " +
                "SUM(prd_doc_count) as prd_doc_count, SUM(api_count) as api_count, " +
                "SUM(java_code_lines) as java_code_lines, SUM(frontend_code_lines) as frontend_code_lines, " +
                "SUM(total_code_lines) as total_code_lines " +
                "FROM member_output WHERE stat_date BETWEEN ? AND ? " +
                "GROUP BY " + groupExpr + " ORDER BY period";
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getRanking(String period, int topN) {
        LocalDate start = "month".equals(period)
                ? LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                : LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        String sql = "SELECT u.id as user_id, u.username, u.real_name, u.department, " +
                "SUM(mo.prd_doc_count) as prd_doc_count, SUM(mo.api_count) as api_count, " +
                "SUM(mo.java_code_lines) as java_code_lines, SUM(mo.frontend_code_lines) as frontend_code_lines, " +
                "SUM(mo.total_code_lines) as total_code_lines, " +
                "(SUM(mo.prd_doc_count)*100 + SUM(mo.api_count)*10 + SUM(mo.total_code_lines)) as total_score " +
                "FROM member_output mo LEFT JOIN sys_user u ON mo.user_id = u.id " +
                "WHERE mo.stat_date >= ? " +
                "GROUP BY u.id, u.username, u.real_name, u.department " +
                "ORDER BY total_score DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, start, topN);
    }

    @Override
    public List<Map<String, Object>> getDetail(String groupBy, LocalDate startDate, LocalDate endDate) {
        if ("department".equals(groupBy)) {
            String sql = "SELECT u.department, " +
                    "SUM(mo.prd_doc_count) as prd_doc_count, SUM(mo.api_count) as api_count, " +
                    "SUM(mo.java_code_lines) as java_code_lines, SUM(mo.frontend_code_lines) as frontend_code_lines, " +
                    "SUM(mo.total_code_lines) as total_code_lines " +
                    "FROM member_output mo LEFT JOIN sys_user u ON mo.user_id = u.id " +
                    "WHERE mo.stat_date BETWEEN ? AND ? " +
                    "GROUP BY u.department ORDER BY prd_doc_count DESC";
            return jdbcTemplate.queryForList(sql, startDate, endDate);
        } else {
            String sql = "SELECT u.id as user_id, u.username, u.real_name, u.department, " +
                    "SUM(mo.prd_doc_count) as prd_doc_count, SUM(mo.api_count) as api_count, " +
                    "SUM(mo.java_code_lines) as java_code_lines, SUM(mo.frontend_code_lines) as frontend_code_lines, " +
                    "SUM(mo.total_code_lines) as total_code_lines " +
                    "FROM member_output mo LEFT JOIN sys_user u ON mo.user_id = u.id " +
                    "WHERE mo.stat_date BETWEEN ? AND ? " +
                    "GROUP BY u.id, u.username, u.real_name, u.department ORDER BY prd_doc_count DESC";
            return jdbcTemplate.queryForList(sql, startDate, endDate);
        }
    }

    @Override
    public Map<String, Object> getPortalOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("visitor_count", queryLong("SELECT COUNT(DISTINCT user_id) FROM browse_history"));
        result.put("browse_count", queryLong("SELECT COUNT(*) FROM browse_history"));
        result.put("article_count", queryLong("SELECT COUNT(*) FROM article WHERE is_deleted = 0"));
        result.put("published_article_count", queryLong("SELECT COUNT(*) FROM article WHERE is_deleted = 0 AND status = 1"));
        result.put("skill_count", queryLong("SELECT COUNT(*) FROM skill WHERE is_deleted = 0"));
        result.put("mcp_count", queryLong("SELECT COUNT(*) FROM mcp_server"));
        result.put("plugin_count", queryLong("SELECT COUNT(*) FROM plugin"));
        result.put("tutorial_count", queryLong("SELECT COUNT(*) FROM tutorial"));
        result.put("question_count", queryLong("SELECT COUNT(*) FROM question WHERE is_deleted = 0"));
        result.put("answer_count", queryLong("SELECT COUNT(*) FROM answer WHERE is_deleted = 0"));
        result.put("comment_count", queryLong("SELECT COUNT(*) FROM community_comment WHERE is_deleted = 0"));
        result.put("total_views", queryLong(
                "SELECT " +
                        "(SELECT COALESCE(SUM(views_count),0) FROM article WHERE is_deleted = 0) + " +
                        "(SELECT COALESCE(SUM(views_count),0) FROM question WHERE is_deleted = 0) + " +
                        "(SELECT COALESCE(SUM(view_count),0) FROM tutorial)"
        ));
        result.put("total_downloads", queryLong(
                "SELECT " +
                        "(SELECT COALESCE(SUM(download_count),0) FROM skill WHERE is_deleted = 0) + " +
                        "(SELECT COALESCE(SUM(download_count),0) FROM plugin)"
        ));
        return result;
    }

    @Override
    public List<Map<String, Object>> getPortalTrend(LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT stat_date,
                       SUM(visitor_count) AS visitor_count,
                       SUM(browse_count) AS browse_count,
                       SUM(article_count) AS article_count,
                       SUM(skill_count) AS skill_count,
                       SUM(question_count) AS question_count
                FROM (
                    SELECT DATE(created_at) AS stat_date,
                           COUNT(DISTINCT user_id) AS visitor_count,
                           COUNT(*) AS browse_count,
                           0 AS article_count,
                           0 AS skill_count,
                           0 AS question_count
                    FROM browse_history
                    WHERE DATE(created_at) BETWEEN ? AND ?
                    GROUP BY DATE(created_at)
                    UNION ALL
                    SELECT DATE(created_at) AS stat_date,
                           0 AS visitor_count,
                           0 AS browse_count,
                           COUNT(*) AS article_count,
                           0 AS skill_count,
                           0 AS question_count
                    FROM article
                    WHERE is_deleted = 0 AND DATE(created_at) BETWEEN ? AND ?
                    GROUP BY DATE(created_at)
                    UNION ALL
                    SELECT DATE(created_at) AS stat_date,
                           0 AS visitor_count,
                           0 AS browse_count,
                           0 AS article_count,
                           COUNT(*) AS skill_count,
                           0 AS question_count
                    FROM skill
                    WHERE is_deleted = 0 AND DATE(created_at) BETWEEN ? AND ?
                    GROUP BY DATE(created_at)
                    UNION ALL
                    SELECT DATE(created_at) AS stat_date,
                           0 AS visitor_count,
                           0 AS browse_count,
                           0 AS article_count,
                           0 AS skill_count,
                           COUNT(*) AS question_count
                    FROM question
                    WHERE is_deleted = 0 AND DATE(created_at) BETWEEN ? AND ?
                    GROUP BY DATE(created_at)
                ) t
                GROUP BY stat_date
                ORDER BY stat_date
                """;
        return jdbcTemplate.queryForList(sql, startDate, endDate, startDate, endDate, startDate, endDate, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getPortalRanking(String type, int topN) {
        String sql = switch (type) {
            case "article" -> """
                    SELECT id, title AS name, author_name AS owner_name, views_count AS primary_value, likes_count AS secondary_value
                    FROM article
                    WHERE is_deleted = 0
                    ORDER BY views_count DESC, likes_count DESC
                    LIMIT ?
                    """;
            case "skill" -> """
                    SELECT id, name, author AS owner_name, download_count AS primary_value, favorite_count AS secondary_value
                    FROM skill
                    WHERE is_deleted = 0
                    ORDER BY download_count DESC, favorite_count DESC
                    LIMIT ?
                    """;
            case "question" -> """
                    SELECT id, title AS name, author_name AS owner_name, views_count AS primary_value, answers_count AS secondary_value
                    FROM question
                    WHERE is_deleted = 0
                    ORDER BY views_count DESC, answers_count DESC
                    LIMIT ?
                    """;
            default -> """
                    SELECT id, title AS name, author AS owner_name, view_count AS primary_value, total_versions AS secondary_value
                    FROM tutorial
                    ORDER BY view_count DESC, total_versions DESC
                    LIMIT ?
                    """;
        };
        return jdbcTemplate.queryForList(sql, topN);
    }

    private Long queryLong(String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0L : value;
    }
}

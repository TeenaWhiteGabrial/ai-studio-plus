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
}

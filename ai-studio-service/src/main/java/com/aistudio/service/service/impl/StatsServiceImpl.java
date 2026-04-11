// ========================================================
// AI生成标记 [Claude Code]
// 生成时间: 2026-03-31
// 文件功能: 统计服务实现类，实现部门和项目统计逻辑
// 修改历史:
//   - 2026-03-31: 创建文件，实现统计服务业务逻辑
// ========================================================
package com.aistudio.service.service.impl;

import com.aistudio.service.mapper.MemberOutputMapper;
import com.aistudio.service.mapper.SysDepartmentMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final MemberOutputMapper memberOutputMapper;
    private final SysDepartmentMapper departmentMapper;
    private final SysUserMapper userMapper;

    @Override
    public List<Map<String, Object>> getDepartmentSummary(List<Long> deptIds, LocalDate startDate, LocalDate endDate) {
        return memberOutputMapper.selectDepartmentSummary(deptIds, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getDepartmentMembers(Long deptId, LocalDate startDate, LocalDate endDate) {
        return memberOutputMapper.selectDepartmentMembers(deptId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getDepartmentRanking(List<Long> deptIds, LocalDate startDate, LocalDate endDate, String sortBy) {
        List<Map<String, Object>> result = memberOutputMapper.selectDepartmentSummary(deptIds, startDate, endDate);

        // 按指定指标排序
        String sortColumn = convertToColumnName(sortBy);
        result.sort((a, b) -> {
            Number va = (Number) a.getOrDefault(sortColumn, 0);
            Number vb = (Number) b.getOrDefault(sortColumn, 0);
            return Long.compare(vb.longValue(), va.longValue()); // 降序
        });

        return result;
    }

    @Override
    public List<Map<String, Object>> getProjectSummary(List<String> projectNames, LocalDate startDate, LocalDate endDate) {
        return memberOutputMapper.selectProjectSummary(projectNames, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getProjectMembers(String projectName, LocalDate startDate, LocalDate endDate) {
        return memberOutputMapper.selectProjectMembers(projectName, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getUserProjectDistribution(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = memberOutputMapper.selectUserProjectDistribution(userId, startDate, endDate);

        // 计算总产出，添加占比
        long total = result.stream()
                .mapToLong(m -> ((Number) m.getOrDefault("totalCodeLines", 0)).longValue())
                .sum();

        if (total > 0) {
            for (Map<String, Object> item : result) {
                long lines = ((Number) item.getOrDefault("totalCodeLines", 0)).longValue();
                item.put("percentage", Math.round(lines * 100.0 / total));
            }
        }

        return result;
    }

    private String convertToColumnName(String sortBy) {
        // 将下划线命名转换为驼峰
        Map<String, String> columnMap = new HashMap<>();
        columnMap.put("total_code_lines", "totalCodeLines");
        columnMap.put("java_code_lines", "javaCodeLines");
        columnMap.put("frontend_code_lines", "frontendCodeLines");
        columnMap.put("prd_doc_count", "prdDocCount");
        columnMap.put("api_count", "apiCount");
        columnMap.put("test_file_count", "testFileCount");

        return columnMap.getOrDefault(sortBy, "totalCodeLines");
    }
}

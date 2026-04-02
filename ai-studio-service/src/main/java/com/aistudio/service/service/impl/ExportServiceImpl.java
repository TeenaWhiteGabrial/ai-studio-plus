// ========================================================
// AI生成标记 [Claude Code]
// 生成时间: 2026-03-31
// 文件功能: 导出服务实现类，实现Excel导出功能
// 修改历史:
//   - 2026-03-31: 创建文件，实现数据导出业务逻辑
// ========================================================
package com.aistudio.service.service.impl;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.aistudio.service.mapper.MemberOutputMapper;
import com.aistudio.service.service.ExportService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final MemberOutputMapper memberOutputMapper;

    // 每批处理的数据量，避免内存溢出
    private static final int BATCH_SIZE = 1000;

    @Override
    public void export(String type, List<Long> deptIds, List<String> projectNames,
                       LocalDate startDate, LocalDate endDate, OutputStream outputStream) {
        try {
            if ("detail".equals(type)) {
                exportDetailStreaming(deptIds, projectNames, startDate, endDate, outputStream);
            } else if ("summary".equals(type)) {
                exportSummary(deptIds, projectNames, startDate, endDate, outputStream);
            } else {
                throw new IllegalArgumentException("不支持的导出类型: " + type);
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 流式导出明细数据（支持大数据量）
     * 使用分页查询 + 流式写入，避免内存溢出
     */
    private void exportDetailStreaming(List<Long> deptIds, List<String> projectNames,
                                       LocalDate startDate, LocalDate endDate,
                                       OutputStream outputStream) {
        // 定义表头
        List<List<String>> headers = Arrays.asList(
                Arrays.asList("日期"),
                Arrays.asList("用户名"),
                Arrays.asList("姓名"),
                Arrays.asList("部门"),
                Arrays.asList("项目"),
                Arrays.asList("PRD文档"),
                Arrays.asList("数据模型文档"),
                Arrays.asList("API文档"),
                Arrays.asList("Java文件数"),
                Arrays.asList("Java代码行"),
                Arrays.asList("API接口数"),
                Arrays.asList("核心服务数"),
                Arrays.asList("实体数量"),
                Arrays.asList("前端组件数"),
                Arrays.asList("前端页面数"),
                Arrays.asList("公共组件数"),
                Arrays.asList("TS代码行"),
                Arrays.asList("前端代码行"),
                Arrays.asList("SQL脚本数"),
                Arrays.asList("测试文件数"),
                Arrays.asList("总代码行"),
                Arrays.asList("备注")
        );

        // 创建 ExcelWriter 支持流式写入
        ExcelWriter excelWriter = EasyExcel.write(outputStream)
                .head(headers)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .build();

        WriteSheet writeSheet = EasyExcel.writerSheet("产出明细").build();

        // 用于累计合计（17个数值列：PRD文档到总代码行）
        long[] sumArray = new long[17];

        try {
            int offset = 0;
            int totalCount = 0;

            while (true) {
                // 分页查询数据
                List<Map<String, Object>> data = memberOutputMapper.selectExportDetailByPage(
                        deptIds, projectNames, startDate, endDate, offset, BATCH_SIZE);

                if (data.isEmpty()) {
                    break;
                }

                // 转换数据并立即写入
                List<List<Object>> rows = new ArrayList<>(data.size());
                for (Map<String, Object> item : data) {
                    List<Object> row = Arrays.asList(
                            item.get("statDate"),
                            item.get("userName"),
                            item.get("realName"),
                            item.get("department"),
                            item.get("projectRootName"),
                            getIntValue(item, "prdDocCount"),
                            getIntValue(item, "dataModelDocCount"),
                            getIntValue(item, "apiDocCount"),
                            getIntValue(item, "javaFileCount"),
                            getIntValue(item, "javaCodeLines"),
                            getIntValue(item, "apiCount"),
                            getIntValue(item, "coreBizServiceCount"),
                            getIntValue(item, "entityCount"),
                            getIntValue(item, "frontendComponentCount"),
                            getIntValue(item, "frontendPageCount"),
                            getIntValue(item, "frontendCommonComponentCount"),
                            getIntValue(item, "tsCodeLines"),
                            getIntValue(item, "frontendCodeLines"),
                            getIntValue(item, "sqlScriptCount"),
                            getIntValue(item, "testFileCount"),
                            getIntValue(item, "totalCodeLines"),
                            item.get("remark")
                    );
                    rows.add(row);

                    // 累加合计（索引5-21是数值列，对应sumArray的0-16）
                    for (int i = 5; i < 22; i++) {
                        Object val = row.get(i);
                        if (val instanceof Number) {
                            sumArray[i - 5] += ((Number) val).longValue();
                        }
                    }
                }

                // 流式写入本批数据
                excelWriter.write(rows, writeSheet);

                totalCount += data.size();
                offset += BATCH_SIZE;

                log.info("已导出 {} 条数据", totalCount);

                // 清理内存
                rows.clear();
                data.clear();

                // 最后一页不足BATCH_SIZE时结束
                if (data.size() < BATCH_SIZE) {
                    break;
                }
            }

            // 添加合计行
            if (totalCount > 0) {
                List<Object> sumRow = new ArrayList<>();
                sumRow.add("合计");
                sumRow.add("");
                sumRow.add("");
                sumRow.add("");
                sumRow.add("");
                for (long sum : sumArray) {
                    sumRow.add(sum);
                }
                sumRow.add("");

                excelWriter.write(Collections.singletonList(sumRow), writeSheet);
                log.info("导出完成，共 {} 条数据（含合计行）", totalCount + 1);
            }

        } finally {
            // 必须关闭writer，否则文件可能损坏
            excelWriter.finish();
        }
    }

    private void exportSummary(List<Long> deptIds, List<String> projectNames,
                               LocalDate startDate, LocalDate endDate, OutputStream outputStream) {
        // 获取汇总数据（按部门或项目分组）
        List<Map<String, Object>> data;
        String sheetName;

        if (deptIds != null && !deptIds.isEmpty()) {
            data = memberOutputMapper.selectDepartmentSummary(deptIds, startDate, endDate);
            sheetName = "部门汇总";
        } else if (projectNames != null && !projectNames.isEmpty()) {
            data = memberOutputMapper.selectProjectSummary(projectNames, startDate, endDate);
            sheetName = "项目汇总";
        } else {
            // 默认按部门汇总
            data = memberOutputMapper.selectDepartmentSummary(null, startDate, endDate);
            sheetName = "部门汇总";
        }

        // 定义表头
        List<List<String>> headers = Arrays.asList(
                Arrays.asList(sheetName.equals("部门汇总") ? "部门" : "项目"),
                Arrays.asList("成员数"),
                Arrays.asList("PRD文档"),
                Arrays.asList("数据模型文档"),
                Arrays.asList("API文档"),
                Arrays.asList("Java文件数"),
                Arrays.asList("Java代码行"),
                Arrays.asList("API接口数"),
                Arrays.asList("核心服务数"),
                Arrays.asList("实体数量"),
                Arrays.asList("前端组件数"),
                Arrays.asList("前端页面数"),
                Arrays.asList("公共组件数"),
                Arrays.asList("TS代码行"),
                Arrays.asList("前端代码行"),
                Arrays.asList("SQL脚本数"),
                Arrays.asList("测试文件数"),
                Arrays.asList("总代码行")
        );

        // 转换数据
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String, Object> item : data) {
            rows.add(Arrays.asList(
                    item.get(sheetName.equals("部门汇总") ? "deptName" : "projectName"),
                    getIntValue(item, "memberCount"),
                    getIntValue(item, "prdDocCount"),
                    getIntValue(item, "dataModelDocCount"),
                    getIntValue(item, "apiDocCount"),
                    getIntValue(item, "javaFileCount"),
                    getIntValue(item, "javaCodeLines"),
                    getIntValue(item, "apiCount"),
                    getIntValue(item, "coreBizServiceCount"),
                    getIntValue(item, "entityCount"),
                    getIntValue(item, "frontendComponentCount"),
                    getIntValue(item, "frontendPageCount"),
                    getIntValue(item, "frontendCommonComponentCount"),
                    getIntValue(item, "tsCodeLines"),
                    getIntValue(item, "frontendCodeLines"),
                    getIntValue(item, "sqlScriptCount"),
                    getIntValue(item, "testFileCount"),
                    getIntValue(item, "totalCodeLines")
            ));
        }

        // 写入 Excel
        EasyExcel.write(outputStream)
                .head(headers)
                .sheet(sheetName)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(rows);
    }

    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
}

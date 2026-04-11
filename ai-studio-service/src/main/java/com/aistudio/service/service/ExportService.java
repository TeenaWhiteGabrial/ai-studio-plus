// ========================================================
// AI生成标记 [Claude Code]
// 生成时间: 2026-03-31
// 文件功能: 导出服务接口，定义数据导出方法
// 修改历史:
//   - 2026-03-31: 创建文件，定义导出服务接口
// ========================================================
package com.aistudio.service.service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

public interface ExportService {

    /**
     * 导出产出数据
     *
     * @param type          导出类型: detail-明细, summary-汇总
     * @param deptIds       部门ID列表
     * @param projectNames  项目名称列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @param outputStream  输出流
     */
    void export(String type, List<Long> deptIds, List<String> projectNames,
                LocalDate startDate, LocalDate endDate, OutputStream outputStream);
}

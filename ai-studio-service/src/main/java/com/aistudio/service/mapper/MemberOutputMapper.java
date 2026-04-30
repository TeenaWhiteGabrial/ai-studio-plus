package com.aistudio.service.mapper;

import com.aistudio.service.entity.MemberOutput;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface MemberOutputMapper extends BaseMapper<MemberOutput> {

    @Select("SELECT mo.*, u.username AS user_name, u.username, u.real_name, d.dept_name as department FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE mo.stat_date = #{date}")
    List<Map<String, Object>> selectAllByDate(@Param("date") LocalDate date);

    @Select("SELECT mo.*, u.username AS username, u.real_name AS realName, d.dept_name AS department FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE mo.user_id = #{userId} AND mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY mo.stat_date DESC")
    List<MemberOutput> selectHistoryWithUserInfo(@Param("userId") Long userId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Select("SELECT SUM(prd_doc_count) as prdDocCount, SUM(data_model_doc_count) as dataModelDocCount, " +
            "SUM(api_doc_count) as apiDocCount, SUM(java_file_count) as javaFileCount, " +
            "SUM(java_code_lines) as javaCodeLines, SUM(api_count) as apiCount, " +
            "SUM(core_biz_service_count) as coreBizServiceCount, SUM(entity_count) as entityCount, " +
            "SUM(frontend_component_count) as frontendComponentCount, SUM(frontend_page_count) as frontendPageCount, " +
            "SUM(frontend_common_component_count) as frontendCommonComponentCount, SUM(ts_code_lines) as tsCodeLines, " +
            "SUM(frontend_code_lines) as frontendCodeLines, SUM(sql_script_count) as sqlScriptCount, " +
            "SUM(test_file_count) as testFileCount, SUM(total_code_lines) as totalCodeLines " +
            "FROM member_output " +
            "WHERE user_id = #{userId} AND stat_date BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> selectStatsByUser(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Select("SELECT SUM(prd_doc_count) as prdDocCount, SUM(data_model_doc_count) as dataModelDocCount, " +
            "SUM(api_doc_count) as apiDocCount, SUM(java_file_count) as javaFileCount, " +
            "SUM(java_code_lines) as javaCodeLines, SUM(api_count) as apiCount, " +
            "SUM(core_biz_service_count) as coreBizServiceCount, SUM(entity_count) as entityCount, " +
            "SUM(frontend_component_count) as frontendComponentCount, SUM(frontend_page_count) as frontendPageCount, " +
            "SUM(frontend_common_component_count) as frontendCommonComponentCount, SUM(ts_code_lines) as tsCodeLines, " +
            "SUM(frontend_code_lines) as frontendCodeLines, SUM(sql_script_count) as sqlScriptCount, " +
            "SUM(test_file_count) as testFileCount, SUM(total_code_lines) as totalCodeLines " +
            "FROM member_output " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> selectStatsAll(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // ==================== 部门统计 ====================

    @Select("<script>" +
            "SELECT d.id as deptId, d.dept_name as deptName, " +
            "SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines, " +
            "COUNT(DISTINCT mo.user_id) as memberCount " +
            "FROM sys_department d " +
            "LEFT JOIN sys_user u ON d.id = u.dept_id " +
            "LEFT JOIN member_output mo ON u.id = mo.user_id AND mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "WHERE d.status = 1 " +
            "<if test='deptIds != null and deptIds.size() > 0'>" +
            "AND d.id IN <foreach collection='deptIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</if>" +
            "GROUP BY d.id, d.dept_name " +
            "ORDER BY d.dept_name" +
            "</script>")
    List<Map<String, Object>> selectDepartmentSummary(@Param("deptIds") List<Long> deptIds,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Select("SELECT u.id as userId, u.username, u.git_name as gitName, u.real_name as realName, " +
            "SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines " +
            "FROM sys_user u " +
            "LEFT JOIN member_output mo ON u.id = mo.user_id AND mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "WHERE u.dept_id = #{deptId} AND u.status = 1 " +
            "GROUP BY u.id, u.username, u.git_name, u.real_name " +
            "ORDER BY SUM(mo.total_code_lines) DESC")
    List<Map<String, Object>> selectDepartmentMembers(@Param("deptId") Long deptId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    // ==================== 项目统计 ====================

    @Select("<script>" +
            "SELECT mo.project_root_name as projectName, " +
            "SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines, " +
            "COUNT(DISTINCT mo.user_id) as memberCount " +
            "FROM member_output mo " +
            "WHERE mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND mo.project_root_name IS NOT NULL " +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach>" +
            "</if>" +
            "GROUP BY mo.project_root_name " +
            "ORDER BY SUM(mo.total_code_lines) DESC" +
            "</script>")
    List<Map<String, Object>> selectProjectSummary(@Param("projectNames") List<String> projectNames,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Select("SELECT u.id as userId, u.username, u.git_name as gitName, u.real_name as realName, " +
            "SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines " +
            "FROM member_output mo " +
            "JOIN sys_user u ON mo.user_id = u.id " +
            "WHERE mo.project_root_name = #{projectName} " +
            "AND mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY u.id, u.username, u.git_name, u.real_name " +
            "ORDER BY SUM(mo.total_code_lines) DESC")
    List<Map<String, Object>> selectProjectMembers(@Param("projectName") String projectName,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Select("SELECT mo.project_root_name as projectName, " +
            "SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines " +
            "FROM member_output mo " +
            "WHERE mo.user_id = #{userId} " +
            "AND mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "AND mo.project_root_name IS NOT NULL " +
            "GROUP BY mo.project_root_name " +
            "ORDER BY SUM(mo.total_code_lines) DESC")
    List<Map<String, Object>> selectUserProjectDistribution(@Param("userId") Long userId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    // ==================== 带筛选条件的扩展方法 ====================

    @Select("<script>" +
            "SELECT mo.*, u.username AS user_name, u.username, u.real_name, d.dept_name as department FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE mo.stat_date = #{date} " +
            "<if test='userIds != null and userIds.size() > 0'>" +
            "AND mo.user_id IN <foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</if>" +
            "<if test='deptIds != null and deptIds.size() > 0'>" +
            "AND d.id IN <foreach collection='deptIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</if>" +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach>" +
            "</if>" +
            "</script>")
    List<Map<String, Object>> selectAllByDateWithFilters(@Param("date") LocalDate date,
                                                         @Param("userIds") List<Long> userIds,
                                                         @Param("deptIds") List<Long> deptIds,
                                                         @Param("projectNames") List<String> projectNames);

    @Select("<script>" +
            "SELECT SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines " +
            "FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='userIds != null and userIds.size() > 0'>" +
            "AND mo.user_id IN <foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</if>" +
            "<if test='deptIds != null and deptIds.size() > 0'>" +
            "AND d.id IN <foreach collection='deptIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</if>" +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach>" +
            "</if>" +
            "</script>")
    Map<String, Object> selectStatsAllWithFilters(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("userIds") List<Long> userIds,
                                                  @Param("deptIds") List<Long> deptIds,
                                                  @Param("projectNames") List<String> projectNames);

    @Select("<script>" +
            "SELECT mo.id, mo.user_id, mo.git_name, mo.stat_date, mo.output_type, mo.project_root_name, " +
            "mo.prd_doc_count, mo.data_model_doc_count, mo.api_doc_count, mo.java_file_count, " +
            "mo.java_code_lines, mo.api_count, mo.core_biz_service_count, mo.entity_count, " +
            "mo.frontend_component_count, mo.frontend_page_count, mo.frontend_common_component_count, " +
            "mo.ts_code_lines, mo.frontend_code_lines, mo.sql_script_count, mo.test_file_count, " +
            "mo.total_code_lines, mo.remark, u.username AS user_name, u.username, u.real_name, " +
            "u.dept_id, d.dept_name AS department, u.team_id, t.team_name " +
            "FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "LEFT JOIN sys_team t ON u.team_id = t.id " +
            "WHERE mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='userIds != null and userIds.size() > 0'>" +
            "AND mo.user_id IN <foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "</if>" +
            "<if test='deptIds != null and deptIds.size() > 0'>" +
            "AND u.dept_id IN <foreach collection='deptIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "</if>" +
            "<if test='teamIds != null and teamIds.size() > 0'>" +
            "AND u.team_id IN <foreach collection='teamIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "</if>" +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach> " +
            "</if>" +
            "ORDER BY mo.stat_date DESC, mo.total_code_lines DESC" +
            "</script>")
    List<Map<String, Object>> selectDashboardDetails(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("userIds") List<Long> userIds,
                                                     @Param("deptIds") List<Long> deptIds,
                                                     @Param("teamIds") List<Long> teamIds,
                                                     @Param("projectNames") List<String> projectNames);

    // ==================== 按人员查询产出 ====================

    @Select("<script>" +
            "SELECT u.id as userId, u.username, u.git_name as gitName, u.real_name as realName, d.dept_name as department, " +
            "SUM(mo.prd_doc_count) as prdDocCount, SUM(mo.data_model_doc_count) as dataModelDocCount, " +
            "SUM(mo.api_doc_count) as apiDocCount, SUM(mo.java_file_count) as javaFileCount, " +
            "SUM(mo.java_code_lines) as javaCodeLines, SUM(mo.api_count) as apiCount, " +
            "SUM(mo.core_biz_service_count) as coreBizServiceCount, SUM(mo.entity_count) as entityCount, " +
            "SUM(mo.frontend_component_count) as frontendComponentCount, SUM(mo.frontend_page_count) as frontendPageCount, " +
            "SUM(mo.frontend_common_component_count) as frontendCommonComponentCount, SUM(mo.ts_code_lines) as tsCodeLines, " +
            "SUM(mo.frontend_code_lines) as frontendCodeLines, SUM(mo.sql_script_count) as sqlScriptCount, " +
            "SUM(mo.test_file_count) as testFileCount, SUM(mo.total_code_lines) as totalCodeLines " +
            "FROM sys_user u " +
            "LEFT JOIN member_output mo ON u.id = mo.user_id AND mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE u.status = 1 " +
            "<if test='userIds != null and userIds.size() > 0'>" +
            "AND u.id IN <foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</if>" +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach>" +
            "</if>" +
            "GROUP BY u.id, u.username, u.git_name, u.real_name, d.dept_name " +
            "ORDER BY SUM(mo.total_code_lines) DESC" +
            "</script>")
    List<Map<String, Object>> selectOutputByUsers(@Param("userIds") List<Long> userIds,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("projectNames") List<String> projectNames);

    // ==================== 分页导出查询 ====================

    /**
     * 分页查询导出明细数据（流式导出用）
     */
    @Select("<script>" +
            "SELECT mo.*, u.username AS user_name, u.username, u.real_name, d.dept_name as department FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='deptIds != null and deptIds.size() > 0'>" +
            "AND d.id IN <foreach collection='deptIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "</if>" +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach> " +
            "</if>" +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectExportDetailByPage(@Param("deptIds") List<Long> deptIds,
                                                       @Param("projectNames") List<String> projectNames,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("offset") int offset,
                                                       @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM member_output mo " +
            "LEFT JOIN sys_user u ON mo.user_id = u.id " +
            "LEFT JOIN sys_department d ON u.dept_id = d.id " +
            "WHERE mo.stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='deptIds != null and deptIds.size() > 0'>" +
            "AND d.id IN <foreach collection='deptIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "</if>" +
            "<if test='projectNames != null and projectNames.size() > 0'>" +
            "AND mo.project_root_name IN <foreach collection='projectNames' item='name' open='(' separator=',' close=')'>#{name}</foreach> " +
            "</if>" +
            "</script>")
    long selectExportDetailCount(@Param("deptIds") List<Long> deptIds,
                                 @Param("projectNames") List<String> projectNames,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}

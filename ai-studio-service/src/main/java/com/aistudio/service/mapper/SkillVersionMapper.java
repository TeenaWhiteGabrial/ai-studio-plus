package com.aistudio.service.mapper;

import com.aistudio.service.entity.SkillVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Skill 版本 Mapper
 */
@Mapper
public interface SkillVersionMapper extends BaseMapper<SkillVersion> {

    /**
     * 根据技能ID查询版本列表（按版本号降序）
     */
    @Select("SELECT * FROM skill_version WHERE skill_id = #{skillId} ORDER BY version_number DESC")
    List<SkillVersion> selectBySkillId(@Param("skillId") Long skillId);

    /**
     * 查询最新版本
     */
    @Select("SELECT * FROM skill_version WHERE skill_id = #{skillId} ORDER BY version_number DESC LIMIT 1")
    SkillVersion selectLatestBySkillId(@Param("skillId") Long skillId);

    /**
     * 根据技能ID和版本号查询
     */
    @Select("SELECT * FROM skill_version WHERE skill_id = #{skillId} AND version = #{version}")
    SkillVersion selectBySkillIdAndVersion(@Param("skillId") Long skillId, @Param("version") String version);

    /**
     * 检查版本是否存在
     */
    @Select("SELECT COUNT(*) FROM skill_version WHERE skill_id = #{skillId} AND version = #{version}")
    boolean existsBySkillIdAndVersion(@Param("skillId") Long skillId, @Param("version") String version);

    /**
     * 统计技能版本数量
     */
    @Select("SELECT COUNT(*) FROM skill_version WHERE skill_id = #{skillId}")
    int countBySkillId(@Param("skillId") Long skillId);
}

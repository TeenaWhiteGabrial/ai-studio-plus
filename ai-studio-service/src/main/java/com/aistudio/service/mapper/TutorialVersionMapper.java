package com.aistudio.service.mapper;

import com.aistudio.service.entity.TutorialVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Tutorial 版本 Mapper
 */
@Mapper
public interface TutorialVersionMapper extends BaseMapper<TutorialVersion> {

    /**
     * 根据教程ID查询版本列表（按版本号降序）
     */
    @Select("SELECT * FROM tutorial_version WHERE tutorial_id = #{tutorialId} ORDER BY version_number DESC")
    List<TutorialVersion> selectByTutorialId(@Param("tutorialId") Long tutorialId);

    /**
     * 查询最新版本
     */
    @Select("SELECT * FROM tutorial_version WHERE tutorial_id = #{tutorialId} ORDER BY version_number DESC LIMIT 1")
    TutorialVersion selectLatestByTutorialId(@Param("tutorialId") Long tutorialId);

    /**
     * 根据教程ID和版本号查询
     */
    @Select("SELECT * FROM tutorial_version WHERE tutorial_id = #{tutorialId} AND version = #{version}")
    TutorialVersion selectByTutorialIdAndVersion(@Param("tutorialId") Long tutorialId, @Param("version") String version);

    /**
     * 检查版本是否存在
     */
    @Select("SELECT COUNT(*) FROM tutorial_version WHERE tutorial_id = #{tutorialId} AND version = #{version}")
    boolean existsByTutorialIdAndVersion(@Param("tutorialId") Long tutorialId, @Param("version") String version);

    /**
     * 统计教程版本数量
     */
    @Select("SELECT COUNT(*) FROM tutorial_version WHERE tutorial_id = #{tutorialId}")
    int countByTutorialId(@Param("tutorialId") Long tutorialId);
}

package com.aistudio.service.mapper;

import com.aistudio.service.entity.PluginVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Plugin 版本 Mapper
 */
@Mapper
public interface PluginVersionMapper extends BaseMapper<PluginVersion> {

    /**
     * 根据插件ID查询版本列表（按创建时间降序）
     */
    @Select("SELECT * FROM plugin_version WHERE plugin_id = #{pluginId} ORDER BY created_at DESC")
    List<PluginVersion> selectByPluginId(@Param("pluginId") Long pluginId);

    /**
     * 查询最新版本
     */
    @Select("SELECT * FROM plugin_version WHERE plugin_id = #{pluginId} ORDER BY created_at DESC LIMIT 1")
    PluginVersion selectLatestByPluginId(@Param("pluginId") Long pluginId);

    /**
     * 根据插件ID和版本号查询
     */
    @Select("SELECT * FROM plugin_version WHERE plugin_id = #{pluginId} AND version = #{version}")
    PluginVersion selectByPluginIdAndVersion(@Param("pluginId") Long pluginId, @Param("version") String version);

    /**
     * 检查版本是否存在
     */
    @Select("SELECT COUNT(*) FROM plugin_version WHERE plugin_id = #{pluginId} AND version = #{version}")
    boolean existsByPluginIdAndVersion(@Param("pluginId") Long pluginId, @Param("version") String version);

    /**
     * 统计插件版本数量
     */
    @Select("SELECT COUNT(*) FROM plugin_version WHERE plugin_id = #{pluginId}")
    int countByPluginId(@Param("pluginId") Long pluginId);
}

package com.aistudio.service.mapper;

import com.aistudio.service.entity.Plugin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PluginMapper extends BaseMapper<Plugin> {

    @Update("UPDATE plugin SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(Long id);
}

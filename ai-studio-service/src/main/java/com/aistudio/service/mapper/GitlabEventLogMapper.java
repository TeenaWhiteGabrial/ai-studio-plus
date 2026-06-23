package com.aistudio.service.mapper;

import com.aistudio.service.entity.GitlabEventLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GitlabEventLogMapper extends BaseMapper<GitlabEventLog> {
}

package com.aistudio.service.mapper;

import com.aistudio.service.entity.GitlabCommitFact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GitlabCommitFactMapper extends BaseMapper<GitlabCommitFact> {
}

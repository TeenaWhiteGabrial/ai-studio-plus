package com.aistudio.service.mapper;

import com.aistudio.service.entity.Tutorial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TutorialMapper extends BaseMapper<Tutorial> {

    @Update("UPDATE tutorial SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
}

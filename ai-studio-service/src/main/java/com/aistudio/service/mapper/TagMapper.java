package com.aistudio.service.mapper;

import com.aistudio.service.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("<script>SELECT * FROM tag WHERE 1=1" +
            "<if test='type != null'> AND type = #{type}</if>" +
            " ORDER BY use_count DESC</script>")
    List<Tag> selectTagsByType(@Param("type") String type);
}

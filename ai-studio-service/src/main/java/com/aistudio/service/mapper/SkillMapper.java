package com.aistudio.service.mapper;

import com.aistudio.service.entity.Skill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SkillMapper extends BaseMapper<Skill> {

    @Update("UPDATE skill SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(Long id);

    /**
     * 检查名称是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM skill WHERE name = #{name} AND is_deleted = 0")
    boolean existsByName(@Param("name") String name);

    /**
     * 根据名称查询（未删除的）
     */
    @Select("SELECT * FROM skill WHERE name = #{name} AND is_deleted = 0 LIMIT 1")
    Skill selectByName(@Param("name") String name);

    /**
     * 查询所有未删除的 Skill
     */
    @Select("SELECT * FROM skill WHERE is_deleted = 0")
    List<Skill> selectAllActive();
}

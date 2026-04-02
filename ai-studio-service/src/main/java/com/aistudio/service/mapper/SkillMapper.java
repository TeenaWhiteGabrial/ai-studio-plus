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
     * 根据 GitLab 路径查询 Skill
     */
    @Select("SELECT * FROM skill WHERE gitlab_path = #{path} LIMIT 1")
    Skill selectByGitlabPath(@Param("path") String path);

    /**
     * 查询所有未删除的 Skill
     */
    @Select("SELECT * FROM skill WHERE is_deleted = 0")
    List<Skill> selectAllActive();
}

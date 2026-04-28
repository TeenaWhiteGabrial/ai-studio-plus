package com.aistudio.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aistudio.service.entity.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<Tag> {

    @Insert("<script>" +
            "INSERT INTO article_tag (article_id, tag_id) VALUES " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{articleId}, #{tagId})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);

    @Select("SELECT t.* FROM tag t " +
            "INNER JOIN article_tag at ON at.tag_id = t.id " +
            "WHERE at.article_id = #{articleId}")
    List<Tag> selectByArticleId(@Param("articleId") Long articleId);

    @Select("SELECT tag_id FROM article_tag WHERE article_id = #{articleId}")
    List<Long> selectTagIdsByArticleId(@Param("articleId") Long articleId);

    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    void deleteByArticleId(@Param("articleId") Long articleId);
}

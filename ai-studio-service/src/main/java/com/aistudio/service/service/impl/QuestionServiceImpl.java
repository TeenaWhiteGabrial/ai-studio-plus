package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.QuestionCreateRequest;
import com.aistudio.service.dto.request.QuestionUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Question;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.QuestionMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResult listQuestions(String keyword, Long tagId, String sort, int page, int size) {
        LambdaQueryWrapper<Question> q = new LambdaQueryWrapper<>();
        q.eq(Question::getTakenDown, 0).eq(Question::getIsDeleted, 0);

        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(Question::getTitle, keyword).or().like(Question::getContent, keyword));
        }

        if ("hot".equals(sort)) {
            q.orderByDesc(Question::getViewsCount, Question::getAnswersCount);
        } else {
            q.orderByDesc(Question::getCreatedAt);
        }

        Page<Question> p = new Page<>(page, size);
        Page<Question> result = questionMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional
    public Question getQuestion(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, id)
                .setSql("views_count = views_count + 1"));
        question.setViewsCount(question.getViewsCount() + 1);
        return question;
    }

    @Override
    @Transactional
    public Long createQuestion(QuestionCreateRequest request, Long authorId) {
        SysUser user = userMapper.selectById(authorId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setAuthorId(authorId);
        question.setAuthorName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        question.setTags(request.getTags() != null ? String.join(",", request.getTags()) : "");
        question.setAnswersCount(0);
        question.setViewsCount(0);
        question.setFavoriteCount(0);
        question.setFollowersCount(0);
        question.setHasBestAnswer(0);
        question.setTakenDown(0);
        question.setIsDeleted(0);
        questionMapper.insert(question);

        log.info("创建问题成功: {}, id={}, authorId={}", question.getTitle(), question.getId(), authorId);
        return question.getId();
    }

    @Override
    @Transactional
    public void updateQuestion(Long id, QuestionUpdateRequest request, Long userId) {
        Question question = questionMapper.selectById(id);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }
        if (!question.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限修改他人的问题");
        }

        if (StringUtils.hasText(request.getTitle())) {
            question.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            question.setContent(request.getContent());
        }
        if (request.getTags() != null) {
            question.setTags(String.join(",", request.getTags()));
        }

        questionMapper.updateById(question);
        log.info("更新问题: id={}", id);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        Question question = questionMapper.selectById(id);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }
        if (!question.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限删除他人的问题");
        }

        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, id)
                .set(Question::getIsDeleted, 1));
        log.info("删除问题: id={}", id);
    }

    @Override
    @Transactional
    public void likeQuestion(Long id, Long userId) {
        log.info("问题点赞: id={}, userId={}", id, userId);
    }

    @Override
    public boolean isLiked(Long id, Long userId) {
        return false;
    }

    @Override
    @Transactional
    public void takedown(Long id) {
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, id)
                .set(Question::getTakenDown, 1));
        log.info("问题下架: id={}", id);
    }

    // ========== Console端 ==========
    @Override
    public PageResult listMyQuestions(Long userId, String keyword, Integer status, int page, int size) {
        LambdaQueryWrapper<Question> q = new LambdaQueryWrapper<>();
        q.eq(Question::getAuthorId, userId)
                .eq(Question::getIsDeleted, 0);

        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(Question::getTitle, keyword).or().like(Question::getContent, keyword));
        }

        if (status != null) {
            if (status == 1) {
                q.eq(Question::getHasBestAnswer, 0); // 待解决
            } else if (status == 2) {
                q.eq(Question::getHasBestAnswer, 1); // 已解决
            }
        }

        q.orderByDesc(Question::getCreatedAt);

        Page<Question> p = new Page<>(page, size);
        Page<Question> result = questionMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public Question getQuestionForEdit(Long id, Long userId) {
        Question question = questionMapper.selectById(id);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }
        if (!question.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限查看他人的问题");
        }
        return question;
    }

    // ========== Admin端 ==========
    @Override
    public PageResult listAllQuestions(String keyword, Integer takenDown, int page, int size) {
        LambdaQueryWrapper<Question> q = new LambdaQueryWrapper<>();
        q.eq(Question::getIsDeleted, 0);

        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(Question::getTitle, keyword).or().like(Question::getContent, keyword));
        }

        if (takenDown != null) {
            q.eq(Question::getTakenDown, takenDown);
        }

        q.orderByDesc(Question::getCreatedAt);

        Page<Question> p = new Page<>(page, size);
        Page<Question> result = questionMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public Question getQuestionForAdmin(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }
        return question;
    }

    @Override
    @Transactional
    public void takedownQuestion(Long id, String reason) {
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, id)
                .set(Question::getTakenDown, 1));
        log.info("管理员下架问题: id={}, reason={}", id, reason);
    }

    @Override
    @Transactional
    public void restoreQuestion(Long id) {
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, id)
                .set(Question::getTakenDown, 0));
        log.info("管理员恢复问题: id={}", id);
    }
}

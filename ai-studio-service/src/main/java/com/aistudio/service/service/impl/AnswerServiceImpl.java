package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AnswerCreateRequest;
import com.aistudio.service.entity.Answer;
import com.aistudio.service.entity.Question;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.AnswerMapper;
import com.aistudio.service.mapper.QuestionMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.AnswerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final SysUserMapper userMapper;

    @Override
    public List<Answer> listByQuestionId(Long questionId) {
        LambdaQueryWrapper<Answer> q = new LambdaQueryWrapper<>();
        q.eq(Answer::getQuestionId, questionId)
                .eq(Answer::getIsDeleted, 0)
                .orderByDesc(Answer::getIsBest)
                .orderByDesc(Answer::getLikesCount)
                .orderByDesc(Answer::getCreatedAt);
        return answerMapper.selectList(q);
    }

    @Override
    @Transactional
    public Long createAnswer(Long questionId, AnswerCreateRequest request, Long authorId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }

        SysUser user = userMapper.selectById(authorId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setAuthorId(authorId);
        answer.setAuthorName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        answer.setContent(request.getContent());
        answer.setLikesCount(0);
        answer.setIsBest(0);
        answer.setIsDeleted(0);
        answerMapper.insert(answer);

        // 更新问题回答数
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, questionId)
                .setSql("answers_count = answers_count + 1"));

        log.info("创建回答: id={}, questionId={}, authorId={}", answer.getId(), questionId, authorId);
        return answer.getId();
    }

    @Override
    @Transactional
    public void updateAnswer(Long id, String content, Long userId) {
        Answer answer = answerMapper.selectById(id);
        if (answer == null || answer.getIsDeleted() == 1) {
            throw new BusinessException(404, "回答不存在");
        }
        if (!answer.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限修改他人的回答");
        }

        answer.setContent(content);
        answerMapper.updateById(answer);
        log.info("更新回答: id={}", id);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id, Long userId) {
        Answer answer = answerMapper.selectById(id);
        if (answer == null || answer.getIsDeleted() == 1) {
            throw new BusinessException(404, "回答不存在");
        }
        if (!answer.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限删除他人的回答");
        }

        answerMapper.update(null, new LambdaUpdateWrapper<Answer>()
                .eq(Answer::getId, id)
                .set(Answer::getIsDeleted, 1));

        // 更新问题回答数
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, answer.getQuestionId())
                .setSql("answers_count = GREATEST(answers_count - 1, 0)"));

        log.info("删除回答: id={}", id);
    }

    @Override
    @Transactional
    public void acceptAnswer(Long answerId, Long questionId, Long userId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null || question.getIsDeleted() == 1) {
            throw new BusinessException(404, "问题不存在");
        }
        if (!question.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "只有问题作者可以采纳回答");
        }

        // 取消之前的最佳答案
        answerMapper.update(null, new LambdaUpdateWrapper<Answer>()
                .eq(Answer::getQuestionId, questionId)
                .set(Answer::getIsBest, 0));

        // 设置新最佳答案
        answerMapper.update(null, new LambdaUpdateWrapper<Answer>()
                .eq(Answer::getId, answerId)
                .set(Answer::getIsBest, 1));

        // 更新问题
        questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, questionId)
                .set(Question::getHasBestAnswer, 1)
                .set(Question::getBestAnswerId, answerId));

        log.info("采纳回答: answerId={}, questionId={}", answerId, questionId);
    }

    @Override
    @Transactional
    public void likeAnswer(Long id, Long userId) {
        // 回答点赞暂不实现
        log.info("回答点赞: id={}, userId={}", id, userId);
    }

    @Override
    public boolean isLiked(Long id, Long userId) {
        return false;
    }
}

package com.aistudio.service.schedule;

import com.aistudio.service.mapper.ArticleMapper;
import com.aistudio.service.entity.Article;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章定时发布调度器
 * 每分钟检查一次是否有待发布的文章
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticlePublishScheduler {

    private final ArticleMapper articleMapper;

    /**
     * 每分钟执行一次，检查并发布到期的草稿文章
     * 查询条件: status=0(草稿) 且 published_at <= now
     */
    @Scheduled(cron = "0 * * * * ?")
    public void publishScheduledArticles() {
        try {
            LocalDateTime now = LocalDateTime.now();

            // 查询所有待发布的文章
            LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Article::getStatus, 0)
                    .isNotNull(Article::getPublishedAt)
                    .le(Article::getPublishedAt, now)
                    .eq(Article::getIsDeleted, 0);
            List<Article> articlesToPublish = articleMapper.selectList(wrapper);

            if (articlesToPublish.isEmpty()) {
                return;
            }

            log.info("开始执行定时发布任务，待发布文章数量: {}", articlesToPublish.size());

            int successCount = 0;
            for (Article article : articlesToPublish) {
                try {
                    // 更新文章状态为已发布
                    int updated = articleMapper.update(null,
                            new LambdaUpdateWrapper<Article>()
                                    .eq(Article::getId, article.getId())
                                    .eq(Article::getStatus, 0) // 再次检查，防止重复发布
                                    .set(Article::getStatus, 1)
                    );

                    if (updated > 0) {
                        successCount++;
                        log.info("文章自动发布成功: id={}, title={}", article.getId(), article.getTitle());
                    }
                } catch (Exception e) {
                    log.error("文章自动发布失败: id={}, error={}", article.getId(), e.getMessage());
                }
            }

            log.info("定时发布任务完成，成功发布: {}/{}", successCount, articlesToPublish.size());
        } catch (Exception e) {
            log.error("定时发布任务执行异常", e);
        }
    }
}

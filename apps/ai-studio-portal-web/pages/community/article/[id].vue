<template>
  <div class="article-detail">
    <div class="article-header">
      <h1 class="article-title">{{ article.title }}</h1>
      <div class="article-meta-bar">
        <span class="author">作者: {{ article.authorName }}</span>
        <span class="date">{{ article.date }}</span>
        <span class="views"><i class="el-icon-view"></i> {{ article.viewsCount }}</span>
      </div>
    </div>
    <div class="article-cover" v-if="article.coverImage">
      <img :src="article.coverImage" :alt="article.title" />
    </div>
    <div class="article-content" v-html="article.content"></div>
    <div class="article-actions-bar">
      <el-button :type="article.liked ? 'primary' : 'default'" plain @click="handleLike">
        <i class="el-icon-star-on"></i> {{ article.likesCount }} 点赞
      </el-button>
      <el-button :type="article.favorited ? 'primary' : 'default'" plain @click="handleFavorite">
        <i class="el-icon-bookmark"></i> 收藏
      </el-button>
    </div>
    <div class="comment-section">
      <h3 class="section-title">评论 ({{ comments.length }})</h3>
      <CommentForm @submit="submitComment" />
      <CommentList :comments="comments" @like="likeComment" @delete="deleteComment" />
    </div>
  </div>
</template>

<script setup lang="ts">
import CommentList from '~/components/CommentList.vue'
import CommentForm from '~/components/CommentForm.vue'

useHead({ title: '文章详情 - AI Studio' })

const route = useRoute()
const article = ref({
  id: route.params.id,
  title: 'AI Studio 入门指南',
  content: '<p>这是一篇示例文章内容。在实际应用中，这里会显示文章的完整富文本内容。</p><p>文章内容支持 Markdown 或 HTML 格式渲染。</p>',
  authorName: '张三',
  date: '2026-04-10',
  viewsCount: 1234,
  likesCount: 89,
  coverImage: 'https://via.placeholder.com/800x300/667eea/ffffff?text=Article',
  liked: false,
  favorited: false,
})
const comments = ref<any[]>([])

function handleLike() { article.value.liked = !article.value.liked; article.value.likesCount += article.value.liked ? 1 : -1 }
function handleFavorite() { article.value.favorited = !article.value.favorited }
function submitComment(content: string) { comments.value.push({ id: Date.now(), content, authorName: '当前用户', date: '刚刚', likesCount: 0 }) }
function likeComment(id: number) {}
function deleteComment(id: number) {}
</script>

<style scoped>
.article-detail { display: flex; flex-direction: column; gap: 20px; }
.article-header { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.article-title { font-size: 24px; font-weight: 700; color: #1a1a2e; margin-bottom: 16px; }
.article-meta-bar { display: flex; gap: 20px; font-size: 13px; color: #909399; }
.article-cover { border-radius: 8px; overflow: hidden; max-height: 400px; }
.article-cover img { width: 100%; height: 100%; object-fit: cover; }
.article-content { background: #fff; border-radius: 8px; padding: 32px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); font-size: 15px; line-height: 1.8; color: #4a4a68; }
.article-actions-bar { background: #fff; border-radius: 8px; padding: 16px 24px; display: flex; gap: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.comment-section { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.section-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 2px solid #f0f2f5; }
</style>

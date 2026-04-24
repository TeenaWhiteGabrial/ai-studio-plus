<template>
  <div class="comment-list">
    <div class="comment-title">
      <Icon name="material-symbols:chat-bubble-outline" class="mr-2" />
      评论 ({{ totalCount }})
    </div>

    <!-- 评论列表 -->
    <div class="comments">
      <div v-if="loading" class="text-center py-4 text-gray-400">
        加载中...
      </div>
      <div v-else-if="comments.length === 0" class="text-center py-8 text-gray-400">
        暂无评论，快来抢沙发吧
      </div>
      <div v-else>
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <!-- 主评论 -->
          <div class="comment-main">
            <el-avatar v-if="comment.authorAvatar" :src="comment.authorAvatar" :size="36" />
            <div v-else class="avatar-placeholder">U</div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="author-name">{{ comment.authorName }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              <div class="comment-actions">
                <span class="action-item" @click="handleLikeComment(comment)">
                  <Icon :name="comment.liked ? 'material-symbols:favorite' : 'material-symbols:favorite-outline'" :class="{ 'text-red-500': comment.liked }" />
                  {{ comment.likeCount }}
                </span>
                <span class="action-item" @click="handleReply(comment)">
                  <Icon name="material-symbols:reply-outline" />
                  回复
                </span>
                <span v-if="canDelete(comment)" class="action-item" @click="handleDeleteComment(comment)">
                  <Icon name="material-symbols:delete-outline" />
                  删除
                </span>
              </div>
              <!-- 回复表单 -->
              <div v-if="replyingTo === comment.id" class="reply-form mt-3">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入回复..."
                />
                <div class="reply-form-actions mt-2">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button size="small" type="primary" @click="submitReply(comment)">提交</el-button>
                </div>
              </div>
            </div>
          </div>
          <!-- 子评论 -->
          <div v-if="comment.replies && comment.replies.length" class="comment-replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <el-avatar v-if="reply.authorAvatar" :src="reply.authorAvatar" :size="28" />
              <div v-else class="avatar-placeholder small">U</div>
              <div class="reply-content">
                <div class="comment-header">
                  <span class="author-name">{{ reply.authorName }}</span>
                  <span class="comment-time">{{ formatTime(reply.createTime) }}</span>
                </div>
                <div class="comment-text">{{ reply.content }}</div>
                <div class="comment-actions">
                  <span class="action-item" @click="handleLikeComment(reply)">
                    <Icon :name="reply.liked ? 'material-symbols:favorite' : 'material-symbols:favorite-outline'" :class="{ 'text-red-500': reply.liked }" />
                    {{ reply.likeCount }}
                  </span>
                  <span v-if="canDelete(reply)" class="action-item" @click="handleDeleteComment(reply)">
                    <Icon name="material-symbols:delete-outline" />
                    删除
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Comment } from '~~/shared/types/community'

const props = defineProps<{
  targetType: 'article' | 'question' | 'answer'
  targetId: string
}>()

const emit = defineEmits<{
  commentChange: []
}>()

const { getCommentList, createComment, deleteComment, likeComment } = useCommunity()
const authStore = useAuthStore()

const comments = ref<Comment[]>([])
const loading = ref(false)
const totalCount = ref(0)
const replyingTo = ref<string | null>(null)
const replyContent = ref('')

async function loadComments() {
  loading.value = true
  try {
    const res = await getCommentList(props.targetType, props.targetId)
    comments.value = res || []
    totalCount.value = res?.length || 0
  }
  catch (err) {
    console.error('加载评论失败:', err)
  }
  finally {
    loading.value = false
  }
}

function handleReply(comment: Comment) {
  replyingTo.value = comment.id
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

async function submitReply(parentComment: Comment) {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await createComment({
      targetType: props.targetType,
      targetId: props.targetId,
      parentId: parentComment.id,
      content: replyContent.value
    })
    ElMessage.success('回复成功')
    cancelReply()
    loadComments()
    emit('commentChange')
  }
  catch (err) {
    ElMessage.error('回复失败')
  }
}

async function handleLikeComment(comment: Comment) {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await likeComment(comment.id)
    comment.likeCount++
  }
  catch (err) {
    console.error('点赞失败:', err)
  }
}

async function handleDeleteComment(comment: Comment) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteComment(comment.id)
    ElMessage.success('删除成功')
    loadComments()
    emit('commentChange')
  }
  catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

function canDelete(comment: Comment) {
  return !!authStore.token && (String(authStore.userId) === String(comment.authorId) || authStore.isAdmin())
}

function formatTime(time: string) {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  if (minutes < 1)
    return '刚刚'
  if (minutes < 60)
    return `${minutes} 分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24)
    return `${hours} 小时前`
  const days = Math.floor(hours / 24)
  if (days < 7)
    return `${days} 天前`
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-list {
  @apply mt-6;
}

.comment-title {
  @apply text-lg font-medium text-gray-800 mb-4 flex items-center;
}

.comment-item {
  @apply py-4 border-b border-gray-100 last:border-0;
}

.comment-main {
  @apply flex gap-3;
}

.avatar-placeholder {
  @apply w-9 h-9 rounded-full bg-primary-faint text-primary flex items-center justify-center font-medium;
}

.avatar-placeholder.small {
  @apply w-7 h-7 text-sm;
}

.comment-content {
  @apply flex-1;
}

.comment-header {
  @apply flex items-center gap-2 mb-1;
}

.author-name {
  @apply font-medium text-gray-800 text-sm;
}

.comment-time {
  @apply text-xs text-gray-400;
}

.comment-text {
  @apply text-gray-600 text-sm leading-relaxed;
}

.comment-actions {
  @apply flex items-center gap-4 mt-2;
}

.action-item {
  @apply flex items-center gap-1 text-sm text-gray-400 cursor-pointer hover:text-primary transition-colors;
}

.comment-replies {
  @apply ml-11 mt-3 pl-4 border-l-2 border-gray-100 space-y-3;
}

.reply-item {
  @apply flex gap-2;
}

.reply-content {
  @apply flex-1;
}

.reply-form {
  @apply bg-gray-50 p-3 rounded-lg;
}

.reply-form-actions {
  @apply flex justify-end gap-2;
}
</style>

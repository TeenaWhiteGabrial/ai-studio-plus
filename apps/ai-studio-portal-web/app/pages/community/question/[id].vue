<template>
  <div class="question-detail-page">
    <!-- 主内容区 -->
    <div class="main-area">
      <div v-if="loading" class="text-center py-12">
        加载中...
      </div>
      <div v-else-if="!question" class="text-center py-12">
        问题不存在
      </div>
      <template v-else>
        <!-- 问题内容 -->
        <article class="question-container bg-white rounded-lg p-6">
          <div class="question-header">
            <h1 class="question-title">{{ question.title }}</h1>
            <el-tag v-if="question.isResolved" type="success" size="small">已解决</el-tag>
          </div>
          <div class="question-meta">
            <div class="author-info">
              <el-avatar v-if="question.authorAvatar" :src="question.authorAvatar" :size="32" />
              <span class="author-name">{{ question.authorName }}</span>
            </div>
            <div class="meta-info">
              <span>{{ formatTime(question.createTime) }}</span>
              <span class="split">|</span>
              <span>{{ question.viewCount }} 阅读</span>
            </div>
          </div>
          <!-- 标签 -->
          <div v-if="question.tags && question.tags.length" class="question-tags">
            <el-tag v-for="tag in question.tags" :key="tag" size="small" class="mr-2">
              {{ tag }}
            </el-tag>
          </div>
          <!-- 问题内容 -->
          <div class="question-content" v-html="question.content"></div>
          <!-- 操作栏 -->
          <div class="question-actions">
            <el-button @click="handleFavorite">
              <Icon name="material-symbols:bookmark-outline" class="mr-1" />
              收藏
            </el-button>
          </div>
        </article>

        <!-- 回答列表 -->
        <div class="answer-section bg-white rounded-lg p-6 mt-6">
          <div class="answer-header">
            <h3>
              <Icon name="material-symbols:chat-bubble" class="mr-2" />
              {{ question.answerCount }} 个回答
            </h3>
          </div>

          <!-- 回答列表 -->
          <div v-if="loadingAnswers" class="text-center py-4 text-gray-400">
            加载中...
          </div>
          <div v-else-if="answers.length === 0" class="text-center py-8 text-gray-400">
            暂无回答，快来抢沙发吧
          </div>
          <div v-else class="answer-list">
            <div
              v-for="answer in answers"
              :key="answer.id"
              class="answer-item"
              :class="{ accepted: answer.isAccepted }"
            >
              <div class="answer-author">
                <el-avatar v-if="answer.authorAvatar" :src="answer.authorAvatar" :size="40" />
                <div class="author-info">
                  <span class="author-name">{{ answer.authorName }}</span>
                  <span class="answer-time">{{ formatTime(answer.createTime) }}</span>
                </div>
              </div>
              <div class="answer-content" v-html="answer.content"></div>
              <div class="answer-actions">
                <el-button
                  :type="answer.isAccepted ? 'success' : 'default'"
                  :disabled="question.isResolved || !canAccept"
                  @click="handleAccept(answer)"
                >
                  <Icon name="material-symbols:check-circle" class="mr-1" />
                  {{ answer.isAccepted ? '已采纳' : '采纳' }}
                </el-button>
                <el-button @click="handleLikeAnswer(answer)">
                  <Icon name="material-symbols:favorite-outline" class="mr-1" />
                  {{ answer.likeCount }}
                </el-button>
              </div>
            </div>
          </div>

          <!-- 回答表单 -->
          <div class="answer-form mt-6">
            <h3>
              <Icon name="material-symbols:edit" class="mr-2" />
              撰写回答
            </h3>
            <div v-if="!authStore.token" class="text-center py-4 text-gray-400">
              <span @click="goToLogin" class="text-primary cursor-pointer">登录</span>后即可回答
            </div>
            <div v-else>
              <el-input
                v-model="answerContent"
                type="textarea"
                :rows="6"
                placeholder="请输入回答内容..."
              />
              <div class="mt-4 text-right">
                <el-button type="primary" :loading="submitting" @click="handleSubmitAnswer">
                  提交回答
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Question } from '~~/shared/types/question'
import type { Answer } from '~~/shared/types/question'

const route = useRoute()
const { getQuestionDetail, getAnswerList, createAnswer, acceptAnswer, likeAnswer } = useQuestion()
const { createBrowseHistory } = useCommunity()
const authStore = useAuthStore()

const question = ref<Question | null>(null)
const answers = ref<Answer[]>([])
const loading = ref(true)
const loadingAnswers = ref(false)
const answerContent = ref('')
const submitting = ref(false)

const canAccept = computed(() => {
  return authStore.token && authStore.userId === question.value?.authorId
})

async function loadQuestion() {
  loading.value = true
  const id = route.params.id as string
  try {
    const res = await getQuestionDetail(id)
    question.value = res
    // 创建浏览记录
    if (authStore.token) {
      createBrowseHistory({ targetType: 'question', targetId: id })
    }
  }
  catch (err) {
    console.error('加载问题失败:', err)
  }
  finally {
    loading.value = false
  }
}

async function loadAnswers() {
  loadingAnswers.value = true
  const id = route.params.id as string
  try {
    const res = await getAnswerList(id)
    answers.value = res || []
  }
  catch (err) {
    console.error('加载回答失败:', err)
  }
  finally {
    loadingAnswers.value = false
  }
}

async function handleSubmitAnswer() {
  if (!answerContent.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  if (!question.value)
    return
  submitting.value = true
  try {
    await createAnswer({
      questionId: question.value.id,
      content: answerContent.value
    })
    ElMessage.success('回答成功')
    answerContent.value = ''
    loadAnswers()
    if (question.value) {
      question.value.answerCount++
    }
  }
  catch (err) {
    ElMessage.error('回答失败')
  }
  finally {
    submitting.value = false
  }
}

async function handleAccept(answer: Answer) {
  if (!question.value || question.value.isResolved)
    return
  try {
    const res = await acceptAnswer(question.value.id, answer.id)
    if (res.code === 200) {
      ElMessage.success('已采纳回答')
      question.value.isResolved = true
      question.value.acceptedAnswerId = answer.id
      answer.isAccepted = true
    }
  }
  catch (err) {
    ElMessage.error('操作失败')
  }
}

async function handleLikeAnswer(answer: Answer) {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await likeAnswer(answer.id)
    answer.likeCount++
  }
  catch (err) {
    ElMessage.error('操作失败')
  }
}

function handleFavorite() {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  ElMessage.info('收藏功能开发中')
}

function goToLogin() {
  navigateTo(`/login?redirect=${route.fullPath}`)
}

function formatTime(time: string) {
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadQuestion().then(() => {
    loadAnswers()
  })
})
</script>

<style scoped>
.question-detail-page {
  @apply flex gap-6;
}

.main-area {
  @apply flex-1 min-w-0;
}

.question-container {
  @apply mb-6;
}

.question-header {
  @apply flex items-start gap-3 mb-4;
}

.question-title {
  @apply text-xl font-bold text-gray-800 flex-1;
}

.question-meta {
  @apply flex items-center justify-between mb-4 pb-4 border-b border-gray-100;
}

.author-info {
  @apply flex items-center gap-2;
}

.author-name {
  @apply font-medium text-gray-700;
}

.meta-info {
  @apply flex items-center gap-2 text-sm text-gray-400;
}

.split {
  @apply text-gray-300;
}

.question-tags {
  @apply mb-4;
}

.question-content {
  @apply text-gray-700 leading-relaxed;
}

.question-content :deep(p) {
  @apply mb-4;
}

.question-content :deep(pre) {
  @apply bg-gray-100 p-4 rounded-lg overflow-x-auto mb-4;
}

.question-actions {
  @apply flex items-center gap-4 mt-6 pt-6 border-t border-gray-100;
}

.answer-section {
  @apply mb-6;
}

.answer-header {
  @apply mb-4;
}

.answer-header h3 {
  @apply text-lg font-medium text-gray-800 flex items-center;
}

.answer-list {
  @apply space-y-6;
}

.answer-item {
  @apply pb-6 border-b border-gray-100 last:border-0;
}

.answer-item.accepted {
  @apply bg-green-50 p-4 rounded-lg -mx-4;
}

.answer-author {
  @apply flex items-center gap-3 mb-3;
}

.answer-time {
  @apply text-xs text-gray-400;
}

.answer-content {
  @apply text-gray-700 leading-relaxed mb-4;
}

.answer-content :deep(p) {
  @apply mb-3;
}

.answer-actions {
  @apply flex items-center gap-3;
}

.answer-form {
  @apply pt-6 border-t border-gray-100;
}

.answer-form h3 {
  @apply text-lg font-medium text-gray-800 mb-4 flex items-center;
}
</style>

<template>
  <div class="question-page">
    <div v-if="loading" class="csdn-card csdn-empty">问题加载中...</div>
    <div v-else-if="!question" class="csdn-card csdn-empty">问题不存在或已删除</div>

    <template v-else>
      <article class="csdn-card question-card">
        <header class="question-header">
          <div class="title-row">
            <h1 class="question-title">{{ question.title }}</h1>
            <span class="status-tag" :class="{ resolved: resolved }">{{ resolved ? '已解决' : '待解决' }}</span>
          </div>

          <div class="meta-row">
            <div class="author-meta">
              <span>{{ question.authorName || '匿名用户' }}</span>
              <span class="dot">·</span>
              <span>{{ formatTime(timeValue) }}</span>
            </div>
            <div class="stats-meta">
              <span><Icon name="material-symbols:visibility-outline" size="16" /> {{ viewCount }}</span>
              <span><Icon name="material-symbols:chat-bubble-outline" size="16" /> {{ answerCount }}</span>
            </div>
          </div>
        </header>

        <div class="csdn-prose question-content" v-html="question.content" />

        <div class="action-row">
          <el-button :type="isFavorited ? 'primary' : 'default'" @click="handleFavorite">
            <Icon :name="isFavorited ? 'material-symbols:bookmark' : 'material-symbols:bookmark-outline'" size="17" />
            <span>{{ isFavorited ? '已收藏' : '收藏问题' }}</span>
          </el-button>
        </div>
      </article>

      <section class="csdn-card answer-card">
        <header class="answer-head">
          <h2>全部回答（{{ answers.length }}）</h2>
        </header>

        <div v-if="loadingAnswers" class="csdn-empty">回答加载中...</div>
        <div v-else-if="answers.length === 0" class="csdn-empty">还没有回答，期待你的分享。</div>
        <div v-else class="answer-list">
          <article
            v-for="answer in answers"
            :key="answer.id"
            class="answer-item"
            :class="{ accepted: isAccepted(answer) }"
          >
            <header class="answer-meta">
              <div class="author">
                <span>{{ answer.authorName || '匿名用户' }}</span>
                <span class="dot">·</span>
                <span>{{ formatTime(answer.createTime || answer.createdAt || '') }}</span>
              </div>
              <span v-if="isAccepted(answer)" class="best-badge">最佳答案</span>
            </header>

            <div class="csdn-prose answer-content" v-html="answer.content" />

            <footer class="answer-actions">
              <el-button size="small" @click="handleLikeAnswer(answer)">
                <Icon name="material-symbols:thumb-up-outline" size="16" />
                <span>{{ answer.likeCount ?? answer.likesCount ?? 0 }}</span>
              </el-button>

              <el-button
                v-if="canAccept && !resolved"
                size="small"
                type="success"
                @click="handleAccept(answer)"
              >
                <Icon name="material-symbols:check-circle-outline" size="16" />
                <span>采纳</span>
              </el-button>
            </footer>
          </article>
        </div>
      </section>

      <section class="csdn-card answer-form-card">
        <h3>我要回答</h3>
        <div v-if="!authStore.token" class="csdn-empty">
          请先 <span class="csdn-link" @click="goToLogin">登录</span> 后再回答
        </div>
        <template v-else>
          <el-input
            v-model="answerContent"
            type="textarea"
            :rows="6"
            placeholder="请尽量给出可执行的思路、代码片段或定位方式。"
          />
          <div class="submit-row">
            <el-button type="primary" :loading="submitting" @click="handleSubmitAnswer">提交回答</el-button>
          </div>
        </template>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { Answer, Question } from '~~/shared/types/question'

const route = useRoute()
const authStore = useAuthStore()
const { getQuestionDetail, getAnswerList, createAnswer, acceptAnswer, likeAnswer } = useQuestion()
const { checkFavorite, createFavorite, deleteFavorite, createBrowseHistory } = useCommunity()

const question = ref<Question | null>(null)
const answers = ref<Answer[]>([])

const loading = ref(true)
const loadingAnswers = ref(false)
const submitting = ref(false)

const isFavorited = ref(false)
const answerContent = ref('')

const resolved = computed(() => Boolean(question.value?.isResolved || question.value?.hasBestAnswer))
const viewCount = computed(() => question.value?.viewCount ?? question.value?.viewsCount ?? 0)
const answerCount = computed(() => question.value?.answerCount ?? question.value?.answersCount ?? answers.value.length)
const timeValue = computed(() => question.value?.createTime || question.value?.createdAt || '')

const canAccept = computed(() => {
  if (!authStore.token || !question.value) return false
  return String(authStore.userId || authStore.user_id || '') === String(question.value.authorId)
})

async function loadQuestion() {
  loading.value = true
  const id = route.params.id as string
  try {
    question.value = await getQuestionDetail(id)
    if (authStore.token) {
      createBrowseHistory({ targetType: 'question', targetId: id })
      isFavorited.value = await checkFavorite('question', id)
    }
  } finally {
    loading.value = false
  }
}

async function loadAnswers() {
  loadingAnswers.value = true
  try {
    answers.value = await getAnswerList(route.params.id as string)
  } finally {
    loadingAnswers.value = false
  }
}

function isAccepted(answer: Answer) {
  if (answer.isAccepted || answer.isBest) return true
  if (!question.value?.acceptedAnswerId) return false
  return String(question.value.acceptedAnswerId) === String(answer.id)
}

async function handleSubmitAnswer() {
  if (!authStore.token) {
    goToLogin()
    return
  }
  if (!question.value || !answerContent.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }

  submitting.value = true
  try {
    await createAnswer({
      questionId: String(question.value.id),
      content: answerContent.value.trim()
    })
    answerContent.value = ''
    ElMessage.success('回答已提交')
    await loadAnswers()
  } finally {
    submitting.value = false
  }
}

async function handleAccept(answer: Answer) {
  if (!question.value) return
  await acceptAnswer(question.value.id, answer.id)
  ElMessage.success('已采纳该回答')
  await loadQuestion()
  await loadAnswers()
}

async function handleLikeAnswer(answer: Answer) {
  if (!authStore.token) {
    goToLogin()
    return
  }
  await likeAnswer(answer.id)
  if (answer.likeCount !== undefined) {
    answer.likeCount += 1
  } else if (answer.likesCount !== undefined) {
    answer.likesCount += 1
  }
}

async function handleFavorite() {
  if (!authStore.token) {
    goToLogin()
    return
  }
  if (!question.value) return

  const id = String(question.value.id)
  if (isFavorited.value) {
    await deleteFavorite('question', id)
    isFavorited.value = false
    ElMessage.success('已取消收藏')
    return
  }

  await createFavorite({
    targetType: 'question',
    targetId: id
  })
  isFavorited.value = true
  ElMessage.success('收藏成功')
}

function goToLogin() {
  goLoginPage(route.fullPath)
}

function formatTime(value: string) {
  if (!value) return '刚刚'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '刚刚'
  return date.toLocaleString('zh-CN')
}

onMounted(async () => {
  await loadQuestion()
  await loadAnswers()
})
</script>

<style scoped>
.question-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-card,
.answer-card,
.answer-form-card {
  padding: 18px 22px;
}

.question-header {
  padding-bottom: 12px;
  border-bottom: 1px solid var(--csdn-line);
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.question-title {
  margin: 0;
  font-size: 28px;
  line-height: 1.35;
}

.status-tag {
  flex-shrink: 0;
  border-radius: 999px;
  padding: 2px 10px;
  font-size: 12px;
  color: #b26a00;
  background: #fff6e6;
}

.status-tag.resolved {
  color: #0f7a3f;
  background: #e9f9f0;
}

.meta-row {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}

.author-meta,
.stats-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.stats-meta span {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.dot {
  color: #c0c3cc;
}

.question-content {
  margin-top: 14px;
}

.action-row {
  margin-top: 18px;
}

.answer-head h2,
.answer-form-card h3 {
  margin: 0;
  font-size: 20px;
}

.answer-list {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
}

.answer-item {
  padding: 14px 0;
  border-bottom: 1px solid var(--csdn-line);
}

.answer-item:last-child {
  border-bottom: 0;
}

.answer-item.accepted {
  background: #f8fffb;
}

.answer-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.answer-meta .author {
  font-size: 13px;
  color: var(--csdn-muted);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.best-badge {
  color: #0f7a3f;
  background: #e9f9f0;
  border-radius: 999px;
  font-size: 12px;
  padding: 2px 10px;
}

.answer-content {
  margin-top: 8px;
}

.answer-actions {
  margin-top: 10px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.submit-row {
  margin-top: 10px;
  text-align: right;
}

@media (max-width: 768px) {
  .question-card,
  .answer-card,
  .answer-form-card {
    padding: 16px;
  }

  .question-title {
    font-size: 24px;
  }
}
</style>

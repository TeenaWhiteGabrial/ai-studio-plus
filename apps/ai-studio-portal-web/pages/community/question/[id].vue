<template>
  <div class="question-detail">
    <div class="question-header">
      <h1 class="question-title">{{ question.title }}</h1>
      <div class="question-meta-bar">
        <span class="author">提问者: {{ question.authorName }}</span>
        <span class="date">{{ question.date }}</span>
        <span class="views"><i class="el-icon-view"></i> {{ question.viewsCount }}</span>
        <span class="answers"><i class="el-icon-chat-round"></i> {{ question.answersCount }} 回答</span>
      </div>
      <div class="question-tags">
        <span v-for="tag in question.tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
    </div>
    <div class="question-content">{{ question.content }}</div>

    <!-- 回答列表 -->
    <div class="answers-section">
      <h3 class="section-title">{{ question.answersCount }} 个回答</h3>
      <div v-for="answer in answers" :key="answer.id" class="answer-item">
        <div class="answer-author">
          <el-avatar :size="36">{{ answer.authorName[0] }}</el-avatar>
          <span class="author-name">{{ answer.authorName }}</span>
          <span v-if="answer.isBest" class="best-answer-badge">采纳</span>
        </div>
        <div class="answer-content">{{ answer.content }}</div>
        <div class="answer-footer">
          <span class="date">{{ answer.date }}</span>
          <div class="answer-actions">
            <el-button size="small" plain :type="answer.liked ? 'primary' : 'default'" @click="answer.liked = !answer.liked">
              <i class="el-icon-star-on"></i> {{ answer.likesCount }}
            </el-button>
            <el-button v-if="isAuthor" size="small" type="success" plain @click="acceptAnswer(answer.id)">采纳</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 回答输入 -->
    <div class="answer-form-section">
      <h3 class="section-title">撰写回答</h3>
      <el-input type="textarea" v-model="answerContent" :rows="4" placeholder="请输入您的回答..." />
      <div style="margin-top:12px;text-align:right;">
        <el-button type="primary" @click="submitAnswer">提交回答</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
useHead({ title: '问答详情 - AI Studio' })

const route = useRoute()
const { userInfo } = useUser()
const isAuthor = computed(() => true) // 实际应判断是否问题作者
const answerContent = ref('')

const question = ref({
  id: route.params.id,
  title: '如何在 AI Studio 中创建自定义 Skill？',
  content: '我想要在 AI Studio 平台创建一个自定义的 Skill，请问有什么具体的步骤和注意事项吗？',
  authorName: '张三',
  date: '2026-04-10',
  viewsCount: 234,
  answersCount: 2,
  tags: ['AI Studio', 'Skill', '开发指南'],
})

const answers = ref([
  { id: 1, authorName: '李四', content: '创建 Skill 需要先在控制台注册，然后编写 Skill 配置文件，最后上传到平台。', date: '2026-04-10', likesCount: 12, liked: false, isBest: true },
  { id: 2, authorName: '王五', content: '还需要注意 Skill 的权限配置和版本管理。', date: '2026-04-10', likesCount: 5, liked: false, isBest: false },
])

function submitAnswer() {
  if (!answerContent.value.trim()) return
  answers.value.push({ id: Date.now(), authorName: userInfo.value?.username || '我', content: answerContent.value, date: '刚刚', likesCount: 0, liked: false, isBest: false })
  answerContent.value = ''
}
function acceptAnswer(id: number) {}
</script>

<style scoped>
.question-detail { display: flex; flex-direction: column; gap: 20px; }
.question-header { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.question-title { font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 12px; }
.question-meta-bar { display: flex; gap: 20px; font-size: 13px; color: #909399; margin-bottom: 12px; }
.question-tags { display: flex; gap: 8px; }
.tag { padding: 2px 10px; background: linear-gradient(135deg, rgba(102,126,234,0.1), rgba(118,75,162,0.1)); color: #667eea; border-radius: 4px; font-size: 12px; }
.question-content { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); font-size: 15px; line-height: 1.8; color: #4a4a68; }
.answers-section { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.section-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 2px solid #f0f2f5; }
.answer-item { padding: 20px 0; border-bottom: 1px solid #f5f7fa; }
.answer-item:last-child { border-bottom: none; }
.answer-author { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.author-name { font-weight: 500; color: #4a4a68; font-size: 14px; }
.best-answer-badge { padding: 2px 8px; background: #67c23a; color: #fff; border-radius: 4px; font-size: 11px; }
.answer-content { font-size: 14px; line-height: 1.7; color: #4a4a68; margin-bottom: 12px; }
.answer-footer { display: flex; justify-content: space-between; align-items: center; }
.date { font-size: 12px; color: #c0c4cc; }
.answer-actions { display: flex; gap: 8px; }
.answer-form-section { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
</style>

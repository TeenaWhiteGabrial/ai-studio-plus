<template>
  <div class="write-page">
    <div class="write-card">
      <h2 class="card-title">发布文章</h2>
      <el-form label-position="top">
        <el-form-item label="文章标题">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="文章封面（可选）">
          <el-input v-model="form.coverImage" placeholder="输入封面图片 URL" />
        </el-form-item>
        <el-form-item label="文章摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入文章摘要" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="文章内容">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入文章内容，支持 Markdown 格式" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple placeholder="选择标签" style="width:100%">
            <el-option v-for="tag in availableTags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
useHead({ title: '发布文章 - AI Studio' })

const form = ref({ title: '', coverImage: '', summary: '', content: '', tagIds: [] as number[] })
const availableTags = ref([{ id: 1, name: 'AI' }, { id: 2, name: 'Skill' }, { id: 3, name: 'Plugin' }])

async function handlePublish() {
  if (!form.value.title || !form.value.content) {
    alert('标题和内容不能为空')
    return
  }
  // 调用 API 发布文章
  alert('文章发布成功')
}
</script>

<style scoped>
.write-page { display: flex; flex-direction: column; }
.write-card { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.card-title { font-size: 18px; font-weight: 600; color: #1a1a2e; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid #f0f2f5; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
</style>

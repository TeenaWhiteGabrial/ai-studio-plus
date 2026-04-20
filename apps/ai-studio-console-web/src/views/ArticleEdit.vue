<template>
  <div class="article-edit">
    <el-card>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文章标题" prop="title" required>
          <el-input v-model="form.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-form-item label="文章摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入文章摘要" />
        </el-form-item>

        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="请输入封面图 URL" />
        </el-form-item>

        <el-form-item label="文章标签"">
          <el-select v-model="form.tagIds" multiple placeholder="请选择标签" style="width: 100%">
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文章内容" prop="content" required>
          <el-input v-model="form.content" type="textarea" :rows="15" placeholder="请输入文章内容" />
        </el-form-item>

        <el-form-item label="发布方式" prop="publishType" required>
          <el-radio-group v-model="form.publishType">
            <el-radio :label="0">保存为草稿</el-radio>
            <el-radio :label="1">立即发布</el-radio>
            <el-radio :label="2">定时发布</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.publishType === 2" label="发布时间" prop="scheduledPublishTime" required>
          <el-date-picker
            v-model="form.scheduledPublishTime"
            type="datetime"
            placeholder="选择发布时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { articleApi } from '@/api'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const saving = ref(false)
const isNew = route.params.id === 'new'

const form = ref({
  id: null as number | null,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  tagIds: [] as number[],
  publishType: 0,
  scheduledPublishTime: null as string | null
})

const tags = ref<any[]>([])

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }],
  publishType: [{ required: true, message: '请选择发布方式', trigger: 'change' }],
  scheduledPublishTime: [
    {
      validator: (rule: any, value: string, callback: any) => {
        if (form.value.publishType === 2 && !value) {
          callback(new Error('请选择发布时间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now()
}

const loadTags = async () => {
  // TODO: 实现标签加载
  // 后端需要提供标签查询接口
}

const loadArticle = async () => {
  if (isNew) return

  try {
    const articleId = Number(route.params.id)
    const res = await articleApi.detail(articleId)
    form.value = {
      id: res.id,
      title: res.title,
      summary: res.summary || '',
      content: res.content,
      coverImage: res.coverImage || '',
      tagIds: res.tagIds || [],
      publishType: res.publishType || 0,
      scheduledPublishTime: res.scheduledPublishTime || null
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  saving.value = true
  try {
    const data = { ...form.value }
    delete data.id

    if (isNew) {
      await articleApi.create(data)
      ElMessage.success('创建成功')
    } else {
      await articleApi.update(form.value.id!, data)
      ElMessage.success('更新成功')
    }

    router.push({ name: 'Article' })
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleCancel = () => {
  router.push({ name: 'Article' })
}

onMounted(() => {
  loadTags()
  loadArticle()
})
</script>

<style scoped>
.article-edit {
  padding: 20px;
  max: 1200px;
  margin: 0 auto;
}
.actions {
  margin-top: 30px;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>

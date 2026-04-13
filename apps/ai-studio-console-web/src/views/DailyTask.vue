<template>
  <div class="task-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>每日任务</span>
          <el-button type="primary" @click="addTask">录入任务</el-button>
        </div>
      </template>

      <el-calendar v-model="currentDate">
        <template #date-cell="{ data }">
          <div class="calendar-cell">
            <span class="date-num">{{ data.day.split('-').slice(2).join('') }}</span>
            <div class="task-list">
              <div v-for="task in getTasks(data.day)" :key="task.id" class="task-item">
                <span class="task-content">{{ task.content }}</span>
                <span class="task-hours">{{ task.hours }}h</span>
              </div>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>

    <el-card class="mt-20">
      <template #header>
        <span>任务统计</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">0</div>
            <div class="stat-label">本周完成任务</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">0h</div>
            <div class="stat-label">本周工时</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">0</div>
            <div class="stat-label">本月完成任务</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">0h</div>
            <div class="stat-label">本月工时</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="dialogVisible" title="录入任务" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务日期">
          <el-date-picker v-model="form.date" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="任务内容">
          <el-input v-model="form.content" type="textarea" placeholder="请输入任务内容" />
        </el-form-item>
        <el-form-item label="工时">
          <el-input-number v-model="form.hours" :min="0" :max="24" :step="0.5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTask">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const currentDate = ref(new Date())
const dialogVisible = ref(false)
const form = ref({ date: '', content: '', hours: 0 })

const tasks = ref([
  { id: 1, date: '2026-04-13', content: '完成用户模块开发', hours: 8, status: 'PENDING' },
])

const getTasks = (date: string) => {
  return tasks.value.filter(t => t.date === date)
}

const addTask = () => {
  dialogVisible.value = true
}

const submitTask = () => {
  ElMessage.success('任务录入成功')
  dialogVisible.value = false
}
</script>

<style scoped>
.task-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-20 {
  margin-top: 20px;
}

.calendar-cell {
  height: 100%;
  padding: 4px;
}

.date-num {
  font-size: 12px;
  color: #909399;
}

.task-list {
  margin-top: 4px;
}

.task-item {
  font-size: 11px;
  padding: 2px 4px;
  background: #f0f9eb;
  border-radius: 4px;
  margin-bottom: 2px;
  display: flex;
  justify-content: space-between;
}

.task-hours {
  color: #67c23a;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>

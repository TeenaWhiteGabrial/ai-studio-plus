<template>
  <div>
    <el-card title="今日产出">
      <template #header><span>今日产出（{{ today }}）</span></template>
      <el-descriptions :column="2" border v-if="hasData">
        <el-descriptions-item label="PRD 文档数">{{ output.prd_doc_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="数据模型文档数">{{ output.data_model_doc_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="API 接口文档数">{{ output.api_doc_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Java 文件数">{{ output.java_file_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="Java 代码行数">{{ output.java_code_lines || 0 }}</el-descriptions-item>
        <el-descriptions-item label="API 接口数">{{ output.api_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="核心业务服务数">{{ output.core_biz_service_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="数据库实体数">{{ output.entity_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="前端组件数">{{ output.frontend_component_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="前端页面数">{{ output.frontend_page_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="前端公共组件数">{{ output.frontend_common_component_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="TypeScript 代码行数">{{ output.ts_code_lines || 0 }}</el-descriptions-item>
        <el-descriptions-item label="前端代码行数">{{ output.frontend_code_lines || 0 }}</el-descriptions-item>
        <el-descriptions-item label="SQL 脚本数">{{ output.sql_script_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="测试文件数">{{ output.test_file_count || 0 }}</el-descriptions-item>
        <el-descriptions-item label="总代码行数">{{ output.total_code_lines || 0 }}</el-descriptions-item>
        <el-descriptions-item label="项目根目录名称">{{ output.project_root_name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ output.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="今日尚未录入产出数据" />
    </el-card>

    <el-card title="开放 API 使用说明" style="margin-top: 20px">
      <el-alert type="info" :closable="false" style="margin-bottom: 16px">
        产出数据请通过开放 API 接口提交，无需认证。调用以下接口即可录入今日产出。
      </el-alert>
      <pre style="background: #f5f7fa; padding: 16px; border-radius: 4px; overflow-x: auto;">
<b>接口地址：</b>POST /api/open/output/submit

<b>请求参数：</b>
{
  "username": "zhangsan",
  "stat_date": "2026-03-26",
  "prd_doc_count": 2,
  "data_model_doc_count": 1,
  "api_doc_count": 3,
  "java_file_count": 5,
  "java_code_lines": 300,
  "api_count": 10,
  "core_biz_service_count": 3,
  "entity_count": 4,
  "frontend_component_count": 8,
  "frontend_page_count": 2,
  "frontend_common_component_count": 3,
  "ts_code_lines": 200,
  "frontend_code_lines": 500,
  "sql_script_count": 2,
  "test_file_count": 3,
  "total_code_lines": 1000,
  "remark": "今日工作完成"
}
      </pre>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { outputApi } from '@/api'
import dayjs from 'dayjs'

const today = dayjs().format('YYYY-MM-DD')
const output = ref<any>({})
const hasData = computed(() => Object.keys(output.value).length > 0)

onMounted(async () => {
  const res = await outputApi.today() as any
  if (res.data) {
    output.value = res.data
  }
})
</script>

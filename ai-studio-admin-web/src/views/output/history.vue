<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="日期范围">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
            range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
            @change="loadList" />
        </el-form-item>
        <el-form-item label="项目">
          <el-select-v2 v-model="selectedProjects" :options="projectOptions" placeholder="选择项目"
            multiple clearable collapse-tags style="width: 200px" @change="loadList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="exportData" :loading="exporting">
            <el-icon><Download /></el-icon>导出
          </el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" border height="500">
        <el-table-column prop="stat_date" label="日期" width="100" fixed />
        <el-table-column prop="user_name" label="用户名" width="120" />
        <el-table-column prop="real_name" label="姓名" width="100" />
        <el-table-column prop="department" label="部门" width="150" />
        <el-table-column prop="project_root_name" label="项目" width="150" />
        <el-table-column label="文档类" align="center">
          <el-table-column prop="prd_doc_count" label="PRD文档" width="80" align="center" />
          <el-table-column prop="data_model_doc_count" label="数据模型文档" width="120" align="center" />
          <el-table-column prop="api_doc_count" label="API文档" width="80" align="center" />
        </el-table-column>
        <el-table-column label="Java 后端" align="center">
          <el-table-column prop="java_file_count" label="Java文件数" width="100" align="center" />
          <el-table-column prop="java_code_lines" label="Java代码行" width="100" align="center" />
          <el-table-column prop="api_count" label="API接口数" width="100" align="center" />
          <el-table-column prop="core_biz_service_count" label="核心服务数" width="100" align="center" />
          <el-table-column prop="entity_count" label="实体数量" width="100" align="center" />
        </el-table-column>
        <el-table-column label="前端" align="center">
          <el-table-column prop="frontend_component_count" label="组件数量" width="100" align="center" />
          <el-table-column prop="frontend_page_count" label="页面数量" width="100" align="center" />
          <el-table-column prop="frontend_common_component_count" label="公共组件数" width="100" align="center" />
          <el-table-column prop="ts_code_lines" label="TS代码行" width="100" align="center" />
          <el-table-column prop="frontend_code_lines" label="前端代码行" width="100" align="center" />
        </el-table-column>
        <el-table-column label="其他" align="center">
          <el-table-column prop="sql_script_count" label="SQL脚本数" width="100" align="center" />
          <el-table-column prop="test_file_count" label="测试文件数" width="100" align="center" />
          <el-table-column prop="total_code_lines" label="总代码行" width="100" align="center" />
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Download } from '@element-plus/icons-vue'
import { outputApi } from '@/api'
import dayjs from 'dayjs'

const loading = ref(false)
const exporting = ref(false)
const list = ref<any[]>([])
const dateRange = ref<[string, string]>([
  dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])
const selectedProjects = ref<string[]>([])
const projectOptions = ref<Array<{ label: string; value: string }>>([])

async function loadList() {
  loading.value = true
  try {
    const params: any = { startDate: dateRange.value[0], endDate: dateRange.value[1] }
    if (selectedProjects.value.length > 0) {
      params.projectNames = selectedProjects.value
    }
    const res: any = await outputApi.history(params)
    list.value = res.data || []
    // 提取项目列表
    updateProjectOptions(list.value)
  } finally {
    loading.value = false
  }
}

function updateProjectOptions(data: any[]) {
  const projects = new Set<string>()
  data.forEach((item: any) => {
    if (item.project_root_name) {
      projects.add(item.project_root_name)
    }
  })
  projectOptions.value = Array.from(projects).map(p => ({ label: p, value: p }))
}

async function exportData() {
  exporting.value = true
  try {
    const params: any = {
      type: 'detail',
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    if (selectedProjects.value.length > 0) {
      params.projectNames = selectedProjects.value
    }
    const res: any = await outputApi.export(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `产出统计_明细_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
    link.click()
  } catch (e) {
    console.error('导出失败', e)
  } finally {
    exporting.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}
</style>

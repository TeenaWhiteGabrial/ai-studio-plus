<template>
  <div class="menu-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <div class="header-actions">
            <el-button @click="loadList">刷新</el-button>
            <el-button type="primary" @click="openCreate">新建菜单</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="treeData"
        row-key="id"
        border
        default-expand-all
        class="menu-table"
      >
        <el-table-column prop="name" label="菜单名称" min-width="180" />
        <el-table-column prop="path" label="路由路径" min-width="180" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" min-width="170" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="130" show-overflow-tooltip />
        <el-table-column prop="permission" label="权限标识" min-width="160" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="hidden" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.hidden === 1 ? 'info' : 'success'">
              {{ row.hidden === 1 ? '隐藏' : '显示' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openCreate(row)">新增子菜单</el-button>
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="620px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="上级菜单">
          <el-select v-model="form.parentId" filterable style="width: 100%">
            <el-option label="顶级菜单" :value="0" />
            <el-option
              v-for="item in parentOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
              :disabled="item.disabled"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="form.path" placeholder="例如 /system/menu" />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="form.component" placeholder="例如 menu/index" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名，例如 List" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.permission" placeholder="例如 menu:list" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="显示状态">
          <el-radio-group v-model="form.hidden">
            <el-radio :label="0">显示</el-radio>
            <el-radio :label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuApi, type AdminMenu, type AdminMenuPayload } from '@/api'

interface ParentOption {
  id: number
  label: string
  disabled: boolean
}

const loading = ref(false)
const submitting = ref(false)
const list = ref<AdminMenu[]>([])
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const form = reactive<AdminMenuPayload>({
  parentId: 0,
  name: '',
  path: '',
  component: '',
  icon: '',
  permission: '',
  sort: 0,
  hidden: 0,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
}

const dialogTitle = computed(() => editId.value ? '编辑菜单' : '新建菜单')
const treeData = computed(() => buildTree(list.value))
const parentOptions = computed<ParentOption[]>(() => {
  const disabledIds = editId.value ? collectSelfAndDescendantIds(editId.value, treeData.value) : new Set<number>()
  return flattenMenus(treeData.value).map(item => ({
    id: item.id,
    label: `${'　'.repeat(item.level)}${item.name}`,
    disabled: disabledIds.has(item.id),
  }))
})

function buildTree(source: AdminMenu[]) {
  const map = new Map<number, AdminMenu & { children: AdminMenu[] }>()
  const roots: Array<AdminMenu & { children: AdminMenu[] }> = []

  source
    .slice()
    .sort((a, b) => (a.sort || 0) - (b.sort || 0))
    .forEach(item => {
      map.set(item.id, { ...item, children: [] })
    })

  map.forEach(item => {
    if (item.parentId && map.has(item.parentId)) {
      map.get(item.parentId)!.children.push(item)
    } else {
      roots.push(item)
    }
  })

  return roots
}

function flattenMenus(nodes: AdminMenu[], level = 0): Array<AdminMenu & { level: number }> {
  return nodes.flatMap(item => [
    { ...item, level },
    ...flattenMenus(item.children || [], level + 1),
  ])
}

function collectSelfAndDescendantIds(id: number, nodes: AdminMenu[]) {
  const result = new Set<number>()
  function walk(items: AdminMenu[], active = false) {
    for (const item of items) {
      const matched = active || item.id === id
      if (matched) {
        result.add(item.id)
      }
      walk(item.children || [], matched)
    }
  }
  walk(nodes)
  return result
}

function resetForm(parentId = 0) {
  editId.value = null
  Object.assign(form, {
    parentId,
    name: '',
    path: '',
    component: '',
    icon: '',
    permission: '',
    sort: 0,
    hidden: 0,
  })
  formRef.value?.clearValidate()
}

async function loadList() {
  loading.value = true
  try {
    const res = await menuApi.list() as any
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openCreate(parent?: AdminMenu) {
  resetForm(parent?.id || 0)
  dialogVisible.value = true
}

function openEdit(row: AdminMenu) {
  editId.value = row.id
  Object.assign(form, {
    parentId: row.parentId || 0,
    name: row.name,
    path: row.path || '',
    component: row.component || '',
    icon: row.icon || '',
    permission: row.permission || '',
    sort: row.sort || 0,
    hidden: row.hidden || 0,
  })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (editId.value) {
      await menuApi.update(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await menuApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await loadList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: AdminMenu) {
  await ElMessageBox.confirm(`确认删除菜单「${row.name}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  await menuApi.delete(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

onMounted(loadList)
</script>

<style scoped>
.menu-page {
  min-width: 0;
}

.card-header,
.header-actions {
  display: flex;
  align-items: center;
}

.card-header {
  justify-content: space-between;
}

.header-actions {
  gap: 8px;
}

.menu-table {
  width: 100%;
}
</style>

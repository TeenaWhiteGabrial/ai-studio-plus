<template>
  <div>
    <el-card>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="role_code" label="角色编码" width="150" />
        <el-table-column prop="role_name" label="角色名称" width="150" />
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role_code !== 'SUPER_ADMIN'"
              size="small"
              type="primary"
              @click="openMenuConfig(row)"
            >
              配置菜单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 菜单配置抽屉 -->
    <el-drawer v-model="drawerVisible" :title="`配置菜单 - ${currentRole?.role_name}`" size="400px">
      <div class="menu-tree-container">
        <el-tree
          ref="treeRef"
          :data="menuTree"
          show-checkbox
          node-key="id"
          :props="{ label: 'name', children: 'children' }"
          :default-checked-keys="checkedKeys"
          :check-strictly="false"
        />
      </div>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { roleApi } from '@/api'

const loading = ref(false)
const saving = ref(false)
const list = ref<any[]>([])
const drawerVisible = ref(false)
const currentRole = ref<any>(null)
const menuTree = ref<any[]>([])
const checkedKeys = ref<number[]>([])
const treeRef = ref<any>(null)

async function loadList() {
  loading.value = true
  try {
    const res = await roleApi.list() as any
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function openMenuConfig(row: any) {
  currentRole.value = row
  drawerVisible.value = true
  try {
    const res = await roleApi.menus(row.id) as any
    menuTree.value = res.data || []
    // 提取已选中的菜单ID
    checkedKeys.value = extractCheckedIds(menuTree.value)
  } catch (error) {
    ElMessage.error('加载菜单失败')
  }
}

function extractCheckedIds(tree: any[]): number[] {
  const ids: number[] = []
  function traverse(nodes: any[]) {
    for (const node of nodes) {
      if (node.checked) {
        ids.push(node.id)
      }
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    }
  }
  traverse(tree)
  return ids
}

async function handleSave() {
  if (!currentRole.value) return

  const checkedNodes = treeRef.value?.getCheckedNodes(false, true) || []
  const menuIds = checkedNodes.map((node: any) => node.id)

  saving.value = true
  try {
    await roleApi.updateMenus(currentRole.value.id, menuIds)
    ElMessage.success('保存成功')
    drawerVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.menu-tree-container {
  padding: 10px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

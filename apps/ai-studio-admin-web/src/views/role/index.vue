<template>
  <div>
    <el-card>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="role_code" label="角色编码" width="150" />
        <el-table-column prop="role_name" label="角色名称" width="150" />
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role_code !== 'SUPER_ADMIN'"
              size="small"
              type="primary"
              @click="openMenuConfig(row)"
            >
              配置菜单
            </el-button>
            <el-button
              v-if="row.role_code !== 'SUPER_ADMIN'"
              size="small"
              @click="openUserConfig(row)"
            >
              管理用户
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

    <!-- 用户配置抽屉 -->
    <el-drawer v-model="userDrawerVisible" :title="`管理用户 - ${currentRole?.role_name}`" size="520px">
      <div class="user-toolbar">
        <el-input v-model="userKeyword" clearable placeholder="搜索账号、姓名或 Git 用户名" />
      </div>
      <el-table
        ref="userTableRef"
        :data="filteredUsers"
        v-loading="userLoading"
        row-key="id"
        border
        height="calc(100vh - 240px)"
        @selection-change="handleUserSelectionChange"
      >
        <el-table-column type="selection" width="48" reserve-selection />
        <el-table-column prop="username" label="账号" min-width="110" />
        <el-table-column prop="real_name" label="姓名" min-width="100" />
        <el-table-column prop="git_name" label="Git用户名" min-width="120" />
        <el-table-column prop="dept_name" label="部门" min-width="120" show-overflow-tooltip />
      </el-table>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="userDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="savingUsers" @click="handleSaveUsers">保存</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { roleApi, userApi } from '@/api'

const loading = ref(false)
const saving = ref(false)
const list = ref<any[]>([])
const drawerVisible = ref(false)
const currentRole = ref<any>(null)
const menuTree = ref<any[]>([])
const checkedKeys = ref<number[]>([])
const treeRef = ref<any>(null)
const userDrawerVisible = ref(false)
const userLoading = ref(false)
const savingUsers = ref(false)
const allUsers = ref<any[]>([])
const roleUserIds = ref<number[]>([])
const selectedUserIds = ref<number[]>([])
const userKeyword = ref('')
const userTableRef = ref<any>(null)

const filteredUsers = computed(() => {
  const keyword = userKeyword.value.trim().toLowerCase()
  if (!keyword) return allUsers.value
  return allUsers.value.filter((user) => {
    return [user.username, user.real_name, user.git_name, user.dept_name]
      .some((value) => String(value || '').toLowerCase().includes(keyword))
  })
})

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

async function openUserConfig(row: any) {
  currentRole.value = row
  userDrawerVisible.value = true
  userKeyword.value = ''
  userLoading.value = true
  try {
    const [userRes, roleUserRes] = await Promise.all([
      userApi.list({ page: 1, size: 1000 }) as any,
      roleApi.users(row.id) as any,
    ])
    allUsers.value = (userRes.data?.records || []).filter((user: any) => !(user.role_ids || []).includes(1))
    roleUserIds.value = roleUserRes.data || []
    selectedUserIds.value = [...roleUserIds.value]
    await nextTick()
    syncUserSelection()
  } catch (error) {
    ElMessage.error('加载用户失败')
  } finally {
    userLoading.value = false
  }
}

function syncUserSelection() {
  userTableRef.value?.clearSelection()
  filteredUsers.value.forEach((user) => {
    if (selectedUserIds.value.includes(user.id)) {
      userTableRef.value?.toggleRowSelection(user, true)
    }
  })
}

function handleUserSelectionChange(selection: any[]) {
  selectedUserIds.value = selection.map((user) => user.id)
}

async function handleSaveUsers() {
  if (!currentRole.value) return
  savingUsers.value = true
  try {
    await roleApi.updateUsers(currentRole.value.id, selectedUserIds.value)
    ElMessage.success('保存成功')
    userDrawerVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    savingUsers.value = false
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

.user-toolbar {
  margin-bottom: 12px;
}
</style>

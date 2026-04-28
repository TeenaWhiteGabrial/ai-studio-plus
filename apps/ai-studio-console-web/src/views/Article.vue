<template>
  <div class="article-page" :class="{ 'folder-collapsed': folderPanelCollapsed }">
    <aside class="folder-panel">
      <div class="folder-header">
        <div class="folder-title">
          <span class="folder-icon">
            <el-icon><FolderOpened /></el-icon>
          </span>
          <div v-if="!folderPanelCollapsed">
            <div class="folder-title-text">文件夹</div>
            
          </div>
        </div>
        <div class="folder-header-actions">
          <el-button v-if="!folderPanelCollapsed" text type="primary" title="新建文件夹" @click="openFolderDialog(null)">
            <el-icon><Plus /></el-icon>
          </el-button>
          <el-button text title="收起/展开文件夹" @click="folderPanelCollapsed = !folderPanelCollapsed">
            <el-icon>
              <ArrowRight v-if="folderPanelCollapsed" />
              <ArrowLeft v-else />
            </el-icon>
          </el-button>
        </div>
      </div>

      <template v-if="folderPanelCollapsed">
        <div class="collapsed-nav">
          <button
            class="collapsed-nav-item"
            :class="{ active: selectedFolderId === null }"
            title="全部文章"
            @click="selectFolder(null)"
          >
            <el-icon><House /></el-icon>
          </button>
          <button
            class="collapsed-nav-item"
            :class="{ active: selectedFolderId === -1 }"
            title="未分类"
            @click="selectFolder(-1)"
          >
            <el-icon><Folder /></el-icon>
          </button>
          <button class="collapsed-nav-item primary" title="新建文件夹" @click="openFolderDialog(null)">
            <el-icon><Plus /></el-icon>
          </button>
        </div>
      </template>

      <template v-else>
        <div class="folder-shortcuts">
          <button class="shortcut-item" :class="{ active: selectedFolderId === null }" @click="selectFolder(null)">
            <span>
              <el-icon><House /></el-icon>
              全部文章
            </span>
            <strong>{{ pagination.total }}</strong>
          </button>
          <button class="shortcut-item" :class="{ active: selectedFolderId === -1 }" @click="selectFolder(-1)">
            <span>
              <el-icon><Folder /></el-icon>
              未分类
            </span>
          </button>
        </div>

        <el-tree
          class="folder-tree"
          :data="nestedFolderTree"
          node-key="id"
          highlight-current
          :current-node-key="selectedFolderId || undefined"
          :expand-on-click-node="false"
          :default-expanded-keys="expandedFolderKeys"
          @node-click="handleFolderClick"
        >
          <template #default="{ data }">
            <div class="folder-node">
              <span class="folder-node-label">
                <el-icon><Folder /></el-icon>
                <span>{{ data.label }}</span>
              </span>
              <span class="folder-actions">
                <el-button text size="small" title="新建子文件夹" @click.stop="openFolderDialog(null, data.id)">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button text size="small" title="编辑文件夹" @click.stop="openFolderDialog(data.raw)">
                  编辑
                </el-button>
                <el-button text size="small" type="danger" title="删除文件夹" @click.stop="deleteFolder(data.raw)">
                  删除
                </el-button>
              </span>
            </div>
          </template>
        </el-tree>
      </template>
    </aside>

    <main class="article-main">
      <section class="article-hero">
        <div>
          <div class="eyebrow">Console / Article</div>
          <h1>我的文章</h1>
          <el-breadcrumb class="folder-breadcrumb" separator="/">
            <el-breadcrumb-item>
              <button class="breadcrumb-button" @click="selectFolder(null)">全部文章</button>
            </el-breadcrumb-item>
            <template v-if="selectedFolderId === -1">
              <el-breadcrumb-item>
                <button class="breadcrumb-button current" @click="selectFolder(-1)">未分类</button>
              </el-breadcrumb-item>
            </template>
            <template v-else>
              <el-breadcrumb-item v-for="item in activeFolderPath" :key="item.id">
                <button class="breadcrumb-button" :class="{ current: item.id === selectedFolderId }" @click="selectFolder(item.id)">
                  {{ item.folderName }}
                </button>
              </el-breadcrumb-item>
            </template>
          </el-breadcrumb>
        </div>
        <div class="hero-actions">
          <el-button class="soft-button" @click="loadArticles">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="primary" @click="handleCreate">
            <el-icon><EditPen /></el-icon>
            新建文章
          </el-button>
        </div>
      </section>

      <section class="content-panel">
        <div class="toolbar">
          <el-input v-model="query.keyword" clearable placeholder="搜索标题或摘要" @keyup.enter="loadArticles">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="query.status" clearable placeholder="全部状态" @change="loadArticles">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
          <el-select v-model="query.sort" placeholder="排序" @change="loadArticles">
            <el-option label="最近创建" value="latest" />
            <el-option label="热度最高" value="hot" />
            <el-option label="点赞最多" value="likes" />
            <el-option label="收藏最多" value="favorites" />
          </el-select>
          <el-button type="primary" @click="loadArticles">查询</el-button>
        </div>

        <div class="folder-context">
          <div>
            <strong>{{ activeFolderName }}</strong>
            <span>{{ folderContextText }}</span>
          </div>
          <el-button v-if="selectedFolderId && selectedFolderId > 0" class="soft-button" @click="openFolderDialog(currentFolder)">
            编辑当前文件夹
          </el-button>
        </div>

        <el-table class="article-table" :data="articleList" v-loading="loading">
          <el-table-column min-width="280" label="文章">
            <template #default="{ row }">
              <div class="article-title-cell">
                <div class="cover-thumb" :class="{ empty: !row.coverImage }">
                  <img v-if="row.coverImage" :src="row.coverImage" alt="" />
                  <el-icon v-else><Document /></el-icon>
                </div>
                <div>
                  <div class="article-title-text">{{ row.title }}</div>
                  <div class="article-summary">{{ row.summary || '暂无摘要' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="104">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="light">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="数据" width="190">
            <template #default="{ row }">
              <div class="metrics">
                <span>读 {{ row.viewsCount || 0 }}</span>
                <span>赞 {{ row.likesCount || 0 }}</span>
                <span>藏 {{ row.favoriteCount || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="170">
            <template #default="{ row }">{{ formatTime(row.publishedAt) }}</template>
          </el-table-column>
          <el-table-column label="创建时间" width="170">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleView(row)">查看</el-button>
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 0" link type="success" @click="handlePublish(row)">发布</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadArticles"
          @size-change="loadArticles"
        />
      </section>
    </main>

    <el-dialog v-model="folderDialog.visible" :title="folderDialog.id ? '编辑文件夹' : '新建文件夹'" width="460px" class="folder-dialog">
      <el-form label-width="84px">
        <el-form-item label="上级目录">
          <el-tree-select
            v-model="folderDialog.parentId"
            :data="folderTreeForSelect"
            check-strictly
            clearable
            placeholder="根目录"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="folderDialog.folderName" placeholder="请输入文件夹名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="folderDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveFolder">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  ArrowRight,
  Document,
  EditPen,
  Folder,
  FolderOpened,
  House,
  Plus,
  Refresh,
  Search,
} from '@element-plus/icons-vue'
import { articleApi, type ArticleFolder } from '@/api'

interface FolderNode {
  id: number
  value: number
  label: string
  raw?: ArticleFolder
  children?: FolderNode[]
}

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const folderPanelCollapsed = ref(false)
const articleList = ref<any[]>([])
const folders = ref<ArticleFolder[]>([])
const selectedFolderId = ref<number | null>(null)
const pagination = reactive({ page: 1, size: 10, total: 0 })
const query = reactive({ keyword: '', status: undefined as number | undefined, sort: 'latest' })
const folderDialog = reactive({ visible: false, id: null as number | null, parentId: null as number | null, folderName: '' })

const nestedFolderTree = computed<FolderNode[]>(() => buildFolderTree(folders.value))
const expandedFolderKeys = computed(() => folders.value.filter(folder => folder.parentId == null).map(folder => folder.id))
const folderTreeForSelect = computed<FolderNode[]>(() => buildFolderTree(folders.value.filter(folder => folder.id !== folderDialog.id)))

const currentFolder = computed(() => folders.value.find(folder => folder.id === selectedFolderId.value) || null)

const activeFolderName = computed(() => {
  if (selectedFolderId.value === null) return '全部文章'
  if (selectedFolderId.value === -1) return '未分类'
  return currentFolder.value?.folderName || '当前文件夹'
})

const activeFolderPath = computed<ArticleFolder[]>(() => {
  if (!selectedFolderId.value || selectedFolderId.value < 0) return []
  const path: ArticleFolder[] = []
  let current = currentFolder.value
  while (current) {
    path.unshift(current)
    current = folders.value.find(folder => folder.id === current?.parentId) || null
  }
  return path
})

const folderContextText = computed(() => {
  if (selectedFolderId.value === null) return '展示当前账号下全部文章'
  if (selectedFolderId.value === -1) return '展示尚未归档到文件夹的文章'
  const childCount = folders.value.filter(folder => folder.parentId === selectedFolderId.value).length
  return `${childCount} 个子文件夹`
})

function buildFolderTree(items: ArticleFolder[], parentId: number | null = null): FolderNode[] {
  return items
    .filter(item => (item.parentId ?? null) === parentId)
    .map(item => ({
      id: item.id,
      value: item.id,
      label: item.folderName,
      raw: item,
      children: buildFolderTree(items, item.id),
    }))
}

function getStatusText(status: number) {
  return ({ 0: '草稿', 1: '已发布', 2: '已下架' } as Record<number, string>)[status] || '未知'
}

function getStatusType(status: number) {
  return ({ 0: 'info', 1: 'success', 2: 'danger' } as Record<number, string>)[status] || ''
}

function formatTime(time?: string) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

async function loadFolders() {
  folders.value = await articleApi.folders()
}

async function loadArticles() {
  loading.value = true
  try {
    const params: any = {
      page: pagination.page,
      size: pagination.size,
      keyword: query.keyword || undefined,
      sort: query.sort,
    }
    if (query.status !== undefined) params.status = query.status
    if (selectedFolderId.value && selectedFolderId.value > 0) params.folderId = selectedFolderId.value
    const res = await articleApi.list(params)
    articleList.value = selectedFolderId.value === -1
      ? res.records.filter((item: any) => !item.folderId)
      : res.records
    pagination.total = selectedFolderId.value === -1 ? articleList.value.length : res.total
  } finally {
    loading.value = false
  }
}

function selectFolder(id: number | null) {
  selectedFolderId.value = id
  pagination.page = 1
  router.replace({ name: 'Article', query: id == null ? {} : { folderId: id } })
  loadArticles()
}

function handleFolderClick(data: FolderNode) {
  selectFolder(data.id)
}

function handleCreate() {
  router.push({ name: 'ArticleEdit', params: { id: 'new' }, query: { folderId: selectedFolderId.value && selectedFolderId.value > 0 ? selectedFolderId.value : undefined } })
}

function handleEdit(article: any) {
  router.push({ name: 'ArticleEdit', params: { id: article.id }, query: article.folderId ? { folderId: article.folderId } : {} })
}

function handleView(article: any) {
  router.push({ name: 'ArticleView', params: { id: article.id }, query: article.folderId ? { folderId: article.folderId } : {} })
}

async function handlePublish(article: any) {
  try {
    await ElMessageBox.confirm('确认发布这篇文章吗？', '提示', { type: 'warning' })
    await articleApi.publish(article.id)
    ElMessage.success('发布成功')
    loadArticles()
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error(error.message || '发布失败')
  }
}

async function handleDelete(article: any) {
  try {
    await ElMessageBox.confirm('确认删除这篇文章吗？', '提示', { type: 'warning' })
    await articleApi.delete(article.id)
    ElMessage.success('删除成功')
    loadArticles()
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

function openFolderDialog(folder: ArticleFolder | null, parentId?: number) {
  folderDialog.id = folder?.id || null
  folderDialog.parentId = folder?.parentId ?? parentId ?? null
  folderDialog.folderName = folder?.folderName || ''
  folderDialog.visible = true
}

async function saveFolder() {
  if (!folderDialog.folderName.trim()) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  if (folderDialog.id) {
    await articleApi.updateFolder(folderDialog.id, { folderName: folderDialog.folderName, parentId: folderDialog.parentId })
    ElMessage.success('文件夹已更新')
  } else {
    await articleApi.createFolder({ folderName: folderDialog.folderName, parentId: folderDialog.parentId })
    ElMessage.success('文件夹已创建')
  }
  folderDialog.visible = false
  await loadFolders()
}

async function deleteFolder(folder: ArticleFolder) {
  try {
    await ElMessageBox.confirm(`确认删除文件夹 "${folder.folderName}" 吗？该文件夹下文章会移到未分类。`, '提示', { type: 'warning' })
    await articleApi.deleteFolder(folder.id)
    ElMessage.success('文件夹已删除')
    if (selectedFolderId.value === folder.id) selectedFolderId.value = null
    await loadFolders()
    loadArticles()
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

onMounted(async () => {
  const folderId = route.query.folderId
  if (folderId !== undefined) selectedFolderId.value = Number(folderId)
  await loadFolders()
  await loadArticles()
})
</script>

<style scoped>
.article-page {
  display: grid;
  grid-template-columns: 288px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
  transition: grid-template-columns 0.2s ease;
}

.article-page.folder-collapsed {
  grid-template-columns: 72px minmax(0, 1fr);
}

.folder-panel,
.content-panel,
.article-hero {
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}

.folder-panel {
  position: sticky;
  top: 84px;
  padding: 14px;
  min-height: calc(100vh - 112px);
}

.folder-header,
.folder-title,
.folder-header-actions,
.folder-node,
.folder-node-label,
.article-hero,
.hero-actions,
.toolbar,
.folder-context,
.article-title-cell,
.metrics {
  display: flex;
  align-items: center;
}

.folder-header,
.article-hero,
.folder-context {
  justify-content: space-between;
}

.folder-title {
  gap: 10px;
  min-width: 0;
}

.folder-icon {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: hsl(var(--primary));
  background: hsl(var(--secondary));
}

.folder-title-text {
  font-weight: 700;
}

.folder-title-sub {
  margin-top: 2px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.folder-header-actions {
  gap: 2px;
}

.folder-shortcuts {
  display: grid;
  gap: 8px;
  margin: 16px 0 10px;
}

.shortcut-item,
.collapsed-nav-item,
.breadcrumb-button {
  border: 0;
  background: transparent;
  cursor: pointer;
  color: inherit;
  font: inherit;
}

.shortcut-item {
  height: 40px;
  padding: 0 10px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: hsl(var(--muted-foreground));
}

.shortcut-item span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.shortcut-item:hover,
.shortcut-item.active,
.collapsed-nav-item:hover,
.collapsed-nav-item.active {
  color: hsl(var(--primary));
  background: hsl(var(--secondary));
}

.collapsed-nav {
  display: grid;
  gap: 10px;
  justify-content: center;
  margin-top: 18px;
}

.collapsed-nav-item {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.collapsed-nav-item.primary {
  color: white;
  background: hsl(var(--primary));
}

.folder-tree {
  margin-top: 8px;
}

.folder-tree :deep(.el-tree-node__content) {
  min-height: 38px;
  border-radius: 8px;
  margin: 2px 0;
}

.folder-tree :deep(.el-tree-node__content:hover),
.folder-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: hsl(var(--secondary));
}

.folder-node {
  width: 100%;
  justify-content: space-between;
  gap: 8px;
  padding-right: 4px;
}

.folder-node-label {
  gap: 8px;
  min-width: 0;
}

.folder-node-label span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.folder-actions {
  opacity: 0;
  display: inline-flex;
  align-items: center;
  gap: 2px;
  white-space: nowrap;
}

.folder-node:hover .folder-actions {
  opacity: 1;
}

.article-main {
  min-width: 0;
}

.article-hero {
  padding: 22px 24px;
  margin-bottom: 14px;
  gap: 18px;
}

.eyebrow {
  font-size: 12px;
  color: hsl(var(--primary));
  font-weight: 700;
  letter-spacing: 0;
}

.article-hero h1 {
  margin: 6px 0 8px;
  font-size: 24px;
  line-height: 1.2;
}

.folder-breadcrumb {
  font-size: 13px;
}

.breadcrumb-button {
  padding: 0;
  color: hsl(var(--muted-foreground));
}

.breadcrumb-button:hover,
.breadcrumb-button.current {
  color: hsl(var(--primary));
}

.hero-actions {
  gap: 10px;
}

.soft-button {
  background: hsl(var(--secondary));
  border-color: transparent;
}

.content-panel {
  padding: 16px;
}

.toolbar {
  gap: 10px;
  flex-wrap: wrap;
  padding: 12px;
  border-radius: 8px;
  background: hsl(var(--secondary));
}

.toolbar .el-input {
  width: min(320px, 100%);
}

.toolbar .el-select {
  width: 148px;
}

.folder-context {
  gap: 12px;
  margin: 14px 0;
  padding: 12px 14px;
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}

.folder-context strong {
  margin-right: 10px;
}

.folder-context span {
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.article-table {
  border-radius: 8px;
  overflow: hidden;
}

.article-table :deep(.el-table__header th) {
  background: hsl(var(--secondary));
  color: hsl(var(--foreground));
  font-weight: 700;
}

.article-title-cell {
  gap: 12px;
  min-width: 0;
}

.cover-thumb {
  width: 54px;
  height: 38px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: hsl(var(--secondary));
  display: flex;
  align-items: center;
  justify-content: center;
  color: hsl(var(--muted-foreground));
}

.cover-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-title-text {
  font-weight: 700;
  color: hsl(var(--foreground));
}

.article-summary {
  max-width: 520px;
  margin-top: 4px;
  font-size: 12px;
  color: hsl(var(--muted-foreground));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.metrics {
  gap: 8px;
  color: hsl(var(--muted-foreground));
  font-size: 12px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

@media (max-width: 920px) {
  .article-page,
  .article-page.folder-collapsed {
    grid-template-columns: 1fr;
  }

  .folder-panel {
    position: static;
    min-height: auto;
  }

  .article-hero {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>

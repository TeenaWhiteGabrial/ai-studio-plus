<template>
  <div class="resource-center">
    <!-- 顶部操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">资源中心</h2>
        <p class="page-desc">管理您的 Skills、Plugins、教程和 MCP 服务器</p>
      </div>
      <div class="header-actions">
        <div class="view-toggle">
          <el-button
            :type="viewMode === 'list' ? 'primary' : 'default'"
            :icon="List"
            size="small"
            @click="viewMode = 'list'"
          >
            列表
          </el-button>
          <el-button
            :type="viewMode === 'card' ? 'primary' : 'default'"
            :icon="Grid"
            size="small"
            @click="viewMode = 'card'"
          >
            卡片
          </el-button>
        </div>
        <el-button v-if="activeTab === 'skill'" type="primary" :icon="Plus" @click="openCreateDialog('skill')">新建 Skill</el-button>
        <el-button v-if="activeTab === 'plugin'" type="primary" :icon="Plus" @click="openCreateDialog('plugin')">新建 Plugin</el-button>
      </div>
    </div>

    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" class="resource-tabs">
      <el-tab-pane name="skill">
        <template #label>
          <div class="tab-label">
            <el-icon><Box /></el-icon>
            <span>Skills</span>
            <el-badge :value="skillCount" :hidden="skillCount === 0" type="primary" class="tab-badge" />
          </div>
        </template>
      </el-tab-pane>
      <el-tab-pane name="plugin">
        <template #label>
          <div class="tab-label">
            <el-icon><Connection /></el-icon>
            <span>Plugins</span>
            <el-badge :value="pluginCount" :hidden="pluginCount === 0" type="primary" class="tab-badge" />
          </div>
        </template>
      </el-tab-pane>
      <el-tab-pane name="tutorial">
        <template #label>
          <div class="tab-label">
            <el-icon><Reading /></el-icon>
            <span>教程</span>
            <el-badge :value="tutorialCount" :hidden="tutorialCount === 0" type="primary" class="tab-badge" />
          </div>
        </template>
      </el-tab-pane>
      <el-tab-pane name="mcp">
        <template #label>
          <div class="tab-label">
            <el-icon><Setting /></el-icon>
            <span>MCP</span>
            <el-badge :value="mcpCount" :hidden="mcpCount === 0" type="primary" class="tab-badge" />
          </div>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 内容区域 -->
    <div class="content-area">
      <div v-show="activeTab === 'skill'">
        <SkillTab
          :skill-list="skillList"
          :loading="skillLoading"
          :view-mode="viewMode"
          :pagination="skillPagination"
          :total="skillCount"
          :is-current-user-resource="isCurrentUserResource"
          @view-versions="handleViewVersions"
          @publish-version="openPublishVersionDialog"
          @download="handleDownload"
          @action="(cmd, skill) => handleSkillAction(cmd, skill)"
          @page-change="loadSkills"
        />
      </div>

      <div v-show="activeTab === 'plugin'">
        <PluginTab
          :plugin-list="pluginList"
          :loading="pluginLoading"
          :view-mode="viewMode"
          :pagination="pluginPagination"
          :total="pluginCount"
          :is-current-user-resource="isCurrentUserResource"
          @publish-version="openPublishVersionDialog"
          @download="handleDownload"
          @action="(cmd, plugin) => handlePluginAction(cmd, plugin)"
          @page-change="loadPlugins"
        />
      </div>

      <div v-show="activeTab === 'tutorial'">
        <TutorialTab
          :tutorial-list="tutorialList"
          :loading="tutorialLoading"
          :view-mode="viewMode"
          :pagination="tutorialPagination"
          :total="tutorialCount"
          :is-current-user-resource="isCurrentUserResource"
          @publish-version="openPublishVersionDialog"
          @action="(cmd, tutorial) => handleTutorialAction(cmd, tutorial)"
          @page-change="loadTutorials"
        />
      </div>

      <div v-show="activeTab === 'mcp'">
        <McpTab
          :mcp-list="mcpList"
          :loading="mcpLoading"
          :view-mode="viewMode"
          :pagination="mcpPagination"
          :total="mcpCount"
          @test-connection="handleTestConnection"
          @page-change="loadMcpServers"
        />
      </div>
    </div>

    <!-- 弹窗 -->
    <SkillDialog v-model="showSkillDialog" :edit-data="skillEditData" @saved="loadSkills" />
    <PluginDialog v-model="showPluginDialog" :edit-data="pluginEditData" @saved="loadPlugins" />
    <TutorialDialog v-model="showTutorialDialog" :edit-data="tutorialEditData" @saved="loadTutorials" />
    <VersionListDialog v-model="showVersionsDialog" :version-list="versionList" />
    <PublishVersionDialog
      v-model="showPublishVersionDialog"
      :form-data="publishVersionFormData"
      @published="handlePublished"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Box, Connection, Reading, Setting, Plus, List, Grid,
} from '@element-plus/icons-vue'
import { skillApi, pluginApi, tutorialApi, mcpApi } from '@/api'
import { useResourceCenter } from '@/composables/useResourceCenter'
import SkillTab from '@/components/ResourceCenter/SkillTab.vue'
import PluginTab from '@/components/ResourceCenter/PluginTab.vue'
import TutorialTab from '@/components/ResourceCenter/TutorialTab.vue'
import McpTab from '@/components/ResourceCenter/McpTab.vue'
import SkillDialog from '@/components/ResourceCenter/SkillDialog.vue'
import PluginDialog from '@/components/ResourceCenter/PluginDialog.vue'
import TutorialDialog from '@/components/ResourceCenter/TutorialDialog.vue'
import VersionListDialog from '@/components/ResourceCenter/VersionListDialog.vue'
import PublishVersionDialog from '@/components/ResourceCenter/PublishVersionDialog.vue'

const {
  activeTab, viewMode, currentUserId, isCurrentUserResource,
  skillList, skillLoading, skillCount, skillPagination, loadSkills,
  pluginList, pluginLoading, pluginCount, pluginPagination, loadPlugins,
  tutorialList, tutorialLoading, tutorialCount, tutorialPagination, loadTutorials,
  mcpList, mcpLoading, mcpCount, mcpPagination, loadMcpServers,
} = useResourceCenter()

// ==================== 弹窗状态 ====================

const showSkillDialog = ref(false)
const skillEditData = ref<any>(null)

const showPluginDialog = ref(false)
const pluginEditData = ref<any>(null)

const showTutorialDialog = ref(false)
const tutorialEditData = ref<any>(null)

const showVersionsDialog = ref(false)
const versionList = ref<any[]>([])

const showPublishVersionDialog = ref(false)
const publishVersionFormData = ref<any>(null)

// ==================== 操作处理 ====================

const openCreateDialog = (type: string) => {
  switch (type) {
    case 'skill':
      skillEditData.value = null
      showSkillDialog.value = true
      break
    case 'plugin':
      pluginEditData.value = null
      showPluginDialog.value = true
      break
  }
}

const handleSkillAction = async (command: string, skill: any) => {
  switch (command) {
    case 'edit':
      skillEditData.value = {
        id: skill.id,
        name: skill.name,
        description: skill.description,
        category: skill.category,
        version: skill.version,
        ossUrl: skill.ossUrl || skill.oss_key || '',
        fileSize: skill.fileSize || 0,
        changelog: skill.changelog || '初始版本',
      }
      showSkillDialog.value = true
      break
    case 'delete':
      try {
        await ElMessageBox.confirm('确定删除此 Skill 吗？', '提示', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        })
        await skillApi.delete(skill.id)
        ElMessage.success('删除成功')
        loadSkills()
      } catch (error) {
        if (error !== 'cancel') console.error('删除失败:', error)
      }
      break
  }
}

const handlePluginAction = async (command: string, plugin: any) => {
  switch (command) {
    case 'edit':
      pluginEditData.value = {
        id: plugin.id,
        name: plugin.name,
        description: plugin.description,
        category: plugin.category,
        icon: plugin.icon,
        fileUrl: plugin.fileUrl,
      }
      showPluginDialog.value = true
      break
    case 'delete':
      try {
        await ElMessageBox.confirm('确定删除此 Plugin 吗？', '提示', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        })
        await pluginApi.delete(plugin.id)
        ElMessage.success('删除成功')
        loadPlugins()
      } catch (error) {
        if (error !== 'cancel') console.error('删除失败:', error)
      }
      break
  }
}

const handleTutorialAction = async (command: string, tutorial: any) => {
  switch (command) {
    case 'edit':
      tutorialEditData.value = {
        id: tutorial.id,
        title: tutorial.title,
        description: tutorial.description,
        category: tutorial.category,
        coverImage: tutorial.coverImage,
        contentType: tutorial.contentType,
        content: tutorial.content,
        videoUrl: tutorial.videoUrl,
        zipFileUrl: tutorial.zipFileUrl,
        zipFileName: tutorial.zipFileName,
      }
      showTutorialDialog.value = true
      break
    case 'delete':
      try {
        await ElMessageBox.confirm('确定删除此教程吗？', '提示', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        })
        await tutorialApi.delete(tutorial.id)
        ElMessage.success('删除成功')
        loadTutorials()
      } catch (error) {
        if (error !== 'cancel') console.error('删除失败:', error)
      }
      break
  }
}

const handleViewVersions = async (skill: any) => {
  try {
    const res = await skillApi.versions(skill.id)
    if (res.code === 200) {
      versionList.value = res.data || []
      showVersionsDialog.value = true
    }
  } catch (error) {
    console.error('获取版本失败:', error)
  }
}

const handleDownload = (item: any) => {
  const url = skillApi.getDownloadUrl(item.id, item.version)
  window.open(url, '_blank')
}

const handleTestConnection = async (mcp: any) => {
  try {
    ElMessage.info('正在测试连接...')
    const res = await mcpApi.test(mcp.id)
    if (res.code === 200) {
      ElMessage.success('连接成功！')
    } else {
      ElMessage.error('连接失败：' + (res.message || '未知错误'))
    }
  } catch (error) {
    console.error('测试连接失败:', error)
    ElMessage.error('连接失败')
  }
}

const openPublishVersionDialog = (type: 'skill' | 'plugin' | 'tutorial', resource: any) => {
  if (!isCurrentUserResource(resource)) {
    ElMessage.warning('只能为自己创建的资源发布版本')
    return
  }
  publishVersionFormData.value = {
    type,
    resourceId: resource.id,
    resourceName: resource.name || resource.title,
    currentVersion: resource.latest_version || resource.version || '1.0.0',
    changelog: '',
    ossUrl: '',
    fileSize: 0,
  }
  showPublishVersionDialog.value = true
}

const handlePublished = (type: string) => {
  switch (type) {
    case 'skill': loadSkills(); break
    case 'plugin': loadPlugins(); break
    case 'tutorial': loadTutorials(); break
  }
}
</script>

<!-- 页面级 scoped 样式 -->
<style scoped>
.resource-center {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, hsla(220 70% 50% 0.05), hsla(142 76% 50% 0.05));
  border-radius: 16px;
  padding: var(--ai-space-5);
  margin-bottom: var(--ai-space-5);
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  color: hsl(var(--foreground));
  margin: 0 0 var(--ai-space-2) 0;
  line-height: 1.2;
  background: linear-gradient(135deg, hsl(var(--foreground)), hsl(var(--primary)));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-desc {
  font-size: 14px;
  color: hsl(var(--muted-foreground));
  margin: 0;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: var(--ai-space-3);
  align-items: center;
}

.view-toggle {
  display: flex;
  gap: 4px;
  background: hsl(var(--muted));
  padding: 4px;
  border-radius: 8px;
}

.view-toggle .el-button {
  border-radius: 6px;
  border: none;
  box-shadow: none;
}

.view-toggle .el-button--default {
  background: transparent;
  color: hsl(var(--muted-foreground));
}

.view-toggle .el-button--primary {
  background: hsl(var(--card));
  color: hsl(var(--primary));
}

.resource-tabs {
  background: hsl(var(--muted) / 0.3);
  border-radius: 12px;
  padding: var(--ai-space-1);
  margin-bottom: var(--ai-space-5);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: var(--ai-space-2);
  font-weight: 500;
  padding: var(--ai-space-2) var(--ai-space-3);
  border-radius: 8px;
  transition: all var(--ai-transition-base);
}

.tab-label:hover {
  background: hsl(var(--secondary));
}

.tab-label.active {
  background: hsl(var(--primary) / 0.1);
}

.tab-label .el-icon {
  font-size: 18px;
  color: hsl(var(--primary));
}

.tab-badge {
  margin-left: var(--ai-space-1);
}

.content-area {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--ai-space-4);
    padding: var(--ai-space-4);
  }

  .page-title { font-size: 22px; }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .header-actions .el-button {
    flex: 1;
    height: 42px;
  }
}
</style>

<!-- 非 scoped 样式：需要穿透到子组件和 append-to-body 弹窗 -->
<style src="@/components/ResourceCenter/resource-center.css"></style>

<style>
/* Element Plus 覆盖 */
.resource-center .el-card__body {
  padding: 0;
}

.resource-center .el-tabs__header {
  margin: 0;
  border-bottom: none;
}

.resource-center .el-tabs__nav-wrap::after {
  display: none;
}

.resource-center .el-tabs__item {
  height: auto;
  line-height: 1;
  padding: 0;
  border: none;
  transition: all 0.3s ease;
}

.resource-center .el-tabs__item.is-active {
  color: hsl(var(--primary));
  font-weight: 600;
}

.resource-center .el-tabs__active-bar {
  display: none;
}

.resource-center .el-tag {
  border-radius: 8px;
  font-weight: 500;
  padding: 4px 10px;
  font-size: 12px;
  border: 1px solid hsl(var(--border));
}

.resource-center .el-tag--info {
  background: hsla(220 70% 50% 0.1);
  border-color: hsla(220 70% 50% 0.2);
  color: hsl(220 70% 50%);
}

.resource-center .el-tag--success {
  background: hsla(142 76% 36% 0.1);
  border-color: hsla(142 76% 36% 0.2);
  color: hsl(142 76% 36%);
}

.resource-center .el-tag--danger {
  background: hsla(0 84% 60% 0.1);
  border-color: hsla(0 84% 60% 0.2);
  color: hsl(0 84% 60%);
}

.resource-center .el-tag--warning {
  background: hsla(38 92% 50% 0.1);
  border-color: hsla(38 92% 50% 0.2);
  color: hsl(38 92% 50%);
}

.resource-center .el-badge {
  transform: scale(0.85);
}

.resource-center .el-badge__content {
  font-weight: 600;
  font-size: 11px;
}

/* 弹窗样式（append-to-body 需要 global） */
.el-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.el-dialog__header {
  padding: var(--ai-space-5);
  border-bottom: 1px solid hsl(var(--border));
  background: hsl(var(--muted) / 0.3);
}

.el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: hsl(var(--foreground));
}

.el-dialog__body {
  padding: var(--ai-space-5);
}

.el-dialog__footer {
  padding: var(--ai-space-4) var(--ai-space-5);
  border-top: 1px solid hsl(var(--border));
  background: hsl(var(--muted) / 0.3);
}

/* 表单样式 */
.el-form-item__label {
  font-weight: 500;
  color: hsl(var(--foreground));
}

.el-input__wrapper {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px hsl(var(--primary) / 0.3) inset;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px hsl(var(--primary)) inset;
}

.el-select .el-input__wrapper {
  cursor: pointer;
}

/* 表格样式 */
.el-table {
  border-radius: 12px;
  overflow: hidden;
}

.el-table th {
  background: hsl(var(--muted) / 0.5);
  font-weight: 600;
  color: hsl(var(--foreground));
}

.el-table tr:hover > td {
  background: hsl(var(--secondary) / 0.5);
}
</style>

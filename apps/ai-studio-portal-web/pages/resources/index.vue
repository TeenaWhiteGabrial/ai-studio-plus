<template>
  <div class="resources-page">
    <!-- 顶部筛选栏 -->
    <div class="filter-bar">
      <div class="tab-group">
        <span class="tab active" @click="currentType = 'skill'">Skill</span>
        <span class="tab" @click="currentType = 'plugin'">Plugin</span>
      </div>
      <div class="filter-right">
        <el-select v-model="sort" size="small" style="width:120px;margin-right:8px;">
          <el-option label="最新发布" value="latest" />
          <el-option label="最多下载" value="downloads" />
          <el-option label="最热门的" value="hot" />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索资源..." size="small" style="width:200px" prefix-icon="Search" clearable />
      </div>
    </div>

    <!-- 资源列表 -->
    <div class="resource-grid">
      <div v-for="item in resourceList" :key="item.id" class="resource-card">
        <div class="card-cover">
          <img :src="item.cover" :alt="item.name" />
          <span class="type-badge">{{ item.type.toUpperCase() }}</span>
        </div>
        <div class="card-body">
          <h3 class="card-title">{{ item.name }}</h3>
          <p class="card-desc">{{ item.description }}</p>
          <div class="card-meta">
            <span class="author"><i class="el-icon-user"></i> {{ item.author }}</span>
            <span class="downloads"><i class="el-icon-download"></i> {{ item.downloadCount }}</span>
          </div>
          <div class="card-tags">
            <span v-for="tag in item.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
        <div class="card-footer">
          <el-button size="small" type="primary" plain style="flex:1">查看详情</el-button>
          <el-button size="small" plain>收藏</el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination background layout="prev, pager, next" :total="100" :page-size="12" />
    </div>
  </div>
</template>

<script setup lang="ts">
useHead({ title: '资源中心 - AI Studio' })

const currentType = ref('skill')
const sort = ref('latest')
const keyword = ref('')

const resourceList = ref([
  { id: 1, type: 'skill', name: '代码审查 Skill', description: '基于 AI 的代码审查工具', author: '张三', downloadCount: 1234, cover: 'https://via.placeholder.com/300x180/667eea/ffffff?text=Skill', tags: ['AI', '代码审查'] },
  { id: 2, type: 'plugin', name: '自动化测试 Plugin', description: '一站式自动化测试', author: '李四', downloadCount: 856, cover: 'https://via.placeholder.com/300x180/764ba2/ffffff?text=Plugin', tags: ['测试', '自动化'] },
  { id: 3, type: 'skill', name: '文档生成 Skill', description: '自动生成技术文档', author: '王五', downloadCount: 2341, cover: 'https://via.placeholder.com/300x180/4facfe/ffffff?text=Skill', tags: ['文档', '生成'] },
  { id: 4, type: 'plugin', name: 'CI/CD 集成', description: '快速集成 CI/CD', author: '赵六', downloadCount: 567, cover: 'https://via.placeholder.com/300x180/f093fb/ffffff?text=Plugin', tags: ['CI/CD', 'DevOps'] },
  { id: 5, type: 'skill', name: 'API 文档生成', description: '自动生成 API 文档', author: '孙七', downloadCount: 1892, cover: 'https://via.placeholder.com/300x180/00f2fe/ffffff?text=Skill', tags: ['API', '文档'] },
  { id: 6, type: 'plugin', name: '日志分析 Plugin', description: '智能日志分析工具', author: '周八', downloadCount: 743, cover: 'https://via.placeholder.com/300x180/667eea/ffffff?text=Plugin', tags: ['日志', '分析'] },
  { id: 7, type: 'skill', name: '安全扫描 Skill', description: '代码安全漏洞扫描', author: '吴九', downloadCount: 1567, cover: 'https://via.placeholder.com/300x180/764ba2/ffffff?text=Skill', tags: ['安全', '扫描'] },
  { id: 8, type: 'plugin', name: '性能监控 Plugin', description: '应用性能监控', author: '郑十', downloadCount: 934, cover: 'https://via.placeholder.com/300x180/4facfe/ffffff?text=Plugin', tags: ['监控', '性能'] },
])
</script>

<style scoped>
.resources-page { display: flex; flex-direction: column; gap: 16px; }
.filter-bar { background: #fff; border-radius: 8px; padding: 16px 20px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.tab-group { display: flex; gap: 4px; }
.tab { padding: 6px 20px; border-radius: 6px; font-size: 14px; color: #606266; cursor: pointer; transition: all 0.2s; }
.tab:hover { color: #667eea; }
.tab.active { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; font-weight: 500; }
.resource-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.resource-card { background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); transition: all 0.3s; }
.resource-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.card-cover { height: 140px; position: relative; overflow: hidden; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; }
.type-badge { position: absolute; top: 10px; right: 10px; padding: 2px 8px; background: rgba(0,0,0,0.6); color: #fff; font-size: 10px; border-radius: 4px; }
.card-body { padding: 16px; }
.card-title { font-size: 15px; font-weight: 600; color: #1a1a2e; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-desc { font-size: 12px; color: #909399; margin-bottom: 10px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 16px; font-size: 12px; color: #c0c4cc; margin-bottom: 8px; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.tag { padding: 2px 8px; background: #f0f2f5; border-radius: 4px; font-size: 11px; color: #909399; }
.card-footer { padding: 12px 16px; border-top: 1px solid #f5f7fa; display: flex; gap: 8px; }
.pagination-wrap { text-align: center; padding: 20px; }
@media (max-width: 1100px) { .resource-grid { grid-template-columns: repeat(3, 1fr); } }
</style>

<template>
  <div class="comment-list">
    <div v-for="comment in comments" :key="comment.id" class="comment-item">
      <div class="comment-header">
        <el-avatar :size="32">{{ comment.authorName[0] }}</el-avatar>
        <span class="author-name">{{ comment.authorName }}</span>
        <span class="comment-date">{{ comment.date }}</span>
      </div>
      <div class="comment-body">{{ comment.content }}</div>
      <div class="comment-actions">
        <el-button size="small" plain @click="$emit('like', comment.id)">
          <i class="el-icon-star-on"></i> {{ comment.likesCount }}
        </el-button>
        <el-button size="small" plain @click="replying = comment.id">回复</el-button>
        <el-button v-if="comment.canDelete" size="small" type="danger" plain @click="$emit('delete', comment.id)">删除</el-button>
      </div>
      <!-- 子评论 -->
      <div v-if="comment.children?.length" class="comment-children">
        <div v-for="child in comment.children" :key="child.id" class="comment-item child">
          <div class="comment-header">
            <el-avatar :size="28">{{ child.authorName[0] }}</el-avatar>
            <span class="author-name">{{ child.authorName }}</span>
            <span class="comment-date">{{ child.date }}</span>
          </div>
          <div class="comment-body">{{ child.content }}</div>
          <div class="comment-actions">
            <el-button size="small" plain @click="$emit('like', child.id)">
              <i class="el-icon-star-on"></i> {{ child.likesCount }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ comments: any[] }>()
defineEmits(['like', 'delete'])
const replying = ref<number | null>(null)
</script>

<style scoped>
.comment-list { margin-top: 20px; }
.comment-item { padding: 16px 0; border-bottom: 1px solid #f5f7fa; }
.comment-item:last-child { border-bottom: none; }
.comment-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.author-name { font-weight: 500; color: #4a4a68; font-size: 14px; }
.comment-date { font-size: 12px; color: #c0c4cc; }
.comment-body { font-size: 14px; line-height: 1.6; color: #4a4a68; margin-bottom: 8px; padding-left: 42px; }
.comment-actions { display: flex; gap: 12px; padding-left: 42px; }
.comment-children { margin-left: 40px; padding-left: 16px; border-left: 2px solid #f0f2f5; margin-top: 12px; }
.child .comment-body { padding-left: 36px; }
.child .comment-actions { padding-left: 36px; }
</style>

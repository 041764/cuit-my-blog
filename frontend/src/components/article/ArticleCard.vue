<template>
  <el-card class="article-card" shadow="hover" @click="goToDetail">
    <div class="article-content">
      <div class="article-info">
        <h2 class="article-title">{{ article.title }}</h2>
        <p class="article-summary">{{ article.summary }}</p>
        <div class="article-meta">
          <span class="author">
            <el-avatar :size="30" :src="authorAvatar" class="author-avatar" />
            {{ authorDisplayName }}
          </span>
          <span class="time">
            <el-icon><Clock /></el-icon> {{ formatDate(article.createdAt) }}
          </span>
          <span class="stats">
            <!-- Stats not available in list response yet -->
            <!-- <el-icon><View /></el-icon> {{ article.viewCount || 0 }} -->
            <!-- <el-icon><ChatDotRound /></el-icon> {{ article.commentCount || 0 }} -->
            <!-- <el-icon><Star /></el-icon> {{ article.likeCount || 0 }} -->
          </span>
        </div>
      </div>
      <div v-if="article.coverImage" class="article-cover">
        <el-image :src="coverImageUrl" fit="cover" />
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Clock } from '@element-plus/icons-vue'
import { resolveAssetUrl } from '@/utils/url'

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const authorDisplayName = computed(() => {
  return props.article.author?.nickname || props.article.author?.username || '匿名作者'
})

const authorAvatar = computed(() => {
  const resolved = resolveAssetUrl(props.article.author?.avatar)
  return resolved || DEFAULT_AVATAR
})

const coverImageUrl = computed(() => resolveAssetUrl(props.article.coverImage))

const goToDetail = () => {
  router.push(`/article/${props.article.id}`)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}
</script>

<style scoped>
.article-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.article-content {
  display: flex;
  justify-content: space-between;
}

.article-info {
  flex: 1;
  margin-right: 20px;
}

.article-title {
  margin: 0 0 10px;
  font-size: 20px;
  color: #303133;
}

.article-title:hover {
  color: var(--el-color-primary);
}

.article-summary {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 14px;
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.author-avatar {
  border: 1px solid #ebeef5;
}

.article-cover {
  width: 200px;
  height: 140px;
  flex-shrink: 0;
}

.article-cover .el-image {
  width: 100%;
  height: 100%;
  border-radius: 4px;
}
</style>

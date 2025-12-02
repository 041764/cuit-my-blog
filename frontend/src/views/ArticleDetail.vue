<template>
  <div class="article-detail-container" v-if="article">
    <div class="article-header">
      <h1 class="title">{{ article.title }}</h1>
      <div class="meta">
                <span class="author">
                  <el-icon><User /></el-icon>
                  <router-link
                    v-if="article.author?.id"
                    :to="{ name: 'AuthorProfile', params: { id: article.author.id } }"
                    class="author-link"
                  >
                    {{ authorDisplayName }}
                  </router-link>
                  <span v-else>{{ authorDisplayName }}</span>
                  <FollowButton :user-id="article.author?.id" class="ml-2" />
                </span>
        <span class="time">
          <el-icon><Clock /></el-icon> {{ formatDate(article.createdAt) }}
        </span>
        <!-- <span class="views">
          <el-icon><View /></el-icon> {{ article.viewCount }} 阅读
        </span> -->
      </div>
    </div>

    <div class="article-content">
      <v-md-preview :text="article.content"></v-md-preview>
    </div>

    <CommentList :article-id="article.id" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleById } from '@/api/article'
import { User, Clock, View } from '@element-plus/icons-vue'
import FollowButton from '@/components/interaction/FollowButton.vue'
import CommentList from '@/components/comment/CommentList.vue'

const route = useRoute()
const article = ref(null)

const authorDisplayName = computed(() => {
  return article.value?.author?.nickname || article.value?.author?.username || '匿名作者'
})

onMounted(async () => {
  const id = route.params.id
  if (id) {
    try {
      article.value = await getArticleById(id)
    } catch (error) {
      console.error('Failed to load article', error)
    }
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.article-detail-container {
  background: #fff;
  padding: 30px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.article-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
}

.title {
  font-size: 32px;
  color: #303133;
  margin-bottom: 20px;
}

.meta {
  color: #909399;
  font-size: 14px;
  display: flex;
  gap: 20px;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.author-link {
  color: var(--el-color-primary);
  font-weight: 500;
}

.author-link:hover {
  text-decoration: underline;
}

.ml-2 {
  margin-left: 8px;
}
</style>

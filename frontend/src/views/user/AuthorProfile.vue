<template>
  <div class="author-profile">
    <el-card class="author-card" shadow="never">
      <div v-if="author" class="author-main">
        <el-avatar :size="100" :src="authorAvatar" class="author-avatar" />
        <div class="author-info">
          <div class="author-name-row">
            <div>
              <h2 class="author-name">{{ displayName }}</h2>
              <p class="author-username">@{{ author.username }}</p>
            </div>
            <FollowButton :user-id="author.id" />
          </div>
          <p class="author-bio">{{ bioText }}</p>
        </div>
      </div>
      <el-skeleton v-else animated :rows="3" />
    </el-card>

    <section class="author-articles">
      <div class="section-header">
        <h3>TA的文章</h3>
        <span class="article-count" v-if="total">共 {{ total }} 篇</span>
      </div>

      <div class="article-list" v-loading="articlesLoading">
        <ArticleCard
          v-for="item in articles"
          :key="item.id"
          :article="item"
        />
      </div>
      <el-empty v-if="!articlesLoading && articles.length === 0" description="暂时还没有文章" />

      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="handlePageChange"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ArticleCard from '@/components/article/ArticleCard.vue'
import FollowButton from '@/components/interaction/FollowButton.vue'
import { getUserById, getUserArticles } from '@/api/user'
import { resolveAssetUrl } from '@/utils/url'

const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const route = useRoute()
const router = useRouter()

const author = ref(null)
const articles = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const articlesLoading = ref(false)

const currentAuthorId = computed(() => route.params.id)

const displayName = computed(() => author.value?.nickname || author.value?.username || '匿名作者')
const authorAvatar = computed(() => resolveAssetUrl(author.value?.avatar) || DEFAULT_AVATAR)
const bioText = computed(() => author.value?.bio || '这个作者很低调，暂无简介')

const fetchAuthor = async (id) => {
  if (!id) {
    author.value = null
    return
  }
  try {
    const data = await getUserById(id)
    author.value = data
  } catch (error) {
    ElMessage.error('作者信息加载失败')
    router.push({ name: 'Home' })
  }
}

const applyArticlePage = (pageResult) => {
  if (!pageResult) {
    articles.value = []
    total.value = 0
    return
  }

  if (Array.isArray(pageResult.list)) {
    articles.value = pageResult.list
    total.value = Number(pageResult.total || pageResult.list.length)
    return
  }

  if (Array.isArray(pageResult.records)) {
    articles.value = pageResult.records
    total.value = Number(pageResult.total || pageResult.records.length)
    return
  }

  if (Array.isArray(pageResult)) {
    articles.value = pageResult
    total.value = pageResult.length
    return
  }

  articles.value = []
  total.value = 0
}

const fetchArticles = async (id) => {
  if (!id) {
    articles.value = []
    total.value = 0
    return
  }
  articlesLoading.value = true
  try {
    const res = await getUserArticles(id, {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    applyArticlePage(res)
  } catch (error) {
    ElMessage.error('文章加载失败')
  } finally {
    articlesLoading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchArticles(currentAuthorId.value)
}

const refresh = (id) => {
  if (!id) {
    router.push({ name: 'Home' })
    return
  }
  pageNum.value = 1
  fetchAuthor(id)
  fetchArticles(id)
}

watch(
  () => route.params.id,
  (newId) => {
    refresh(newId)
  }
)

onMounted(() => {
  refresh(currentAuthorId.value)
})
</script>

<style scoped>
.author-profile {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.author-card {
  padding: 24px;
}

.author-main {
  display: flex;
  gap: 20px;
  align-items: center;
}

.author-avatar {
  border: 1px solid #ebeef5;
}

.author-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.author-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.author-name {
  margin: 0;
}

.author-username {
  margin: 4px 0 0;
  color: #909399;
}

.author-bio {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.author-articles {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid #ebeef5;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.article-count {
  color: #909399;
  font-size: 14px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

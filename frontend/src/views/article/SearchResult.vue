<template>
  <div class="search-page">
    <section class="search-hero">
      <div>
        <p class="search-kicker">文章搜索</p>
        <h1 v-if="keyword">“{{ keyword }}”的搜索结果</h1>
        <h1 v-else>请输入关键词开始搜索</h1>
        <p class="search-hint" v-if="keyword">共找到 {{ total }} 篇相关文章</p>
      </div>
      <div class="search-box">
        <el-input
          v-model="localKeyword"
          placeholder="搜索文章标题、摘要或内容"
          :prefix-icon="Search"
          @keyup.enter="submitSearch"
        />
        <el-button type="primary" @click="submitSearch">搜索</el-button>
      </div>
    </section>

    <section class="search-results">
      <ArticleCard
        v-for="article in articles"
        :key="article.id"
        :article="article"
      />
      <el-skeleton v-if="loading" rows="3" animated />
      <el-empty v-else-if="!articles.length && keyword" description="没有找到相关文章" />
      <el-empty v-else-if="!keyword" description="输入关键词开始搜索" />
    </section>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getArticles } from '@/api/article'
import ArticleCard from '@/components/article/ArticleCard.vue'

const route = useRoute()
const router = useRouter()

const articles = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(Number(route.query.pageNum) || 1)
const keyword = ref(route.query.keyword ? String(route.query.keyword) : '')
const localKeyword = ref(keyword.value)
const loading = ref(false)

const goHome = () => {
  if (route.name === 'ArticleSearch') {
    router.replace({ name: 'Home' }).catch(() => {})
  } else if (route.name !== 'Home') {
    router.push({ name: 'Home' }).catch(() => {})
  }
}

const loadArticles = async () => {
  if (!keyword.value) {
    goHome()
    return
  }

  loading.value = true
  try {
    const data = await getArticles({
      keyword: keyword.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    if (data) {
      if (Array.isArray(data.list)) {
        articles.value = data.list
        total.value = Number(data.total || data.list.length)
      } else if (Array.isArray(data.records)) {
        articles.value = data.records
        total.value = Number(data.total || data.records.length)
      } else if (Array.isArray(data)) {
        articles.value = data
        total.value = data.length
      } else {
        articles.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('搜索文章失败', error)
  } finally {
    loading.value = false
  }
}

const syncFromRoute = () => {
  keyword.value = route.query.keyword ? String(route.query.keyword) : ''
  localKeyword.value = keyword.value
  currentPage.value = route.query.pageNum ? Number(route.query.pageNum) : 1
}

watch(
  () => [route.query.keyword, route.query.pageNum],
  () => {
    syncFromRoute()
    loadArticles()
  }
)

const navigateToSearch = (page = 1) => {
  const trimmed = localKeyword.value.trim()
  if (!trimmed) {
    goHome()
    return
  }

  const currentKeyword = route.query.keyword ? String(route.query.keyword) : ''
  const currentPageParam = route.query.pageNum ? Number(route.query.pageNum) : 1

  if (trimmed === currentKeyword && page === currentPageParam) {
    // 条件未变化，直接刷新列表
    keyword.value = trimmed
    currentPage.value = page
    loadArticles()
    return
  }

  router.push({
    name: 'ArticleSearch',
    query: {
      keyword: trimmed,
      pageNum: page
    }
  })
}

const submitSearch = () => {
  navigateToSearch(1)
}

const handlePageChange = (page) => {
  navigateToSearch(page)
}

onMounted(() => {
  syncFromRoute()
  loadArticles()
})
</script>

<style scoped>
.search-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-hero {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  border: 1px solid var(--page-border-color);
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.05);
}

.search-kicker {
  margin: 0;
  font-size: 13px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--page-muted-color);
}

.search-hero h1 {
  margin: 8px 0;
  font-size: 26px;
  color: #1f2d3d;
}

.search-hint {
  margin: 0;
  color: #64748b;
}

.search-box {
  display: flex;
  gap: 12px;
  width: 100%;
  max-width: 420px;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

@media (max-width: 640px) {
  .search-box {
    flex-direction: column;
  }
}
</style>

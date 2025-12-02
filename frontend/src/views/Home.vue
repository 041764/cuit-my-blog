<template>
  <div class="home-layout">
    <section class="home-hero">
      <div class="hero-text">
        <p class="hero-kicker">创作中心</p>
        <h1>分享你的文章</h1>
        <p class="hero-subtitle">灵感一触即发，现在就去创作吧。</p>
      </div>
      <el-button type="primary" :icon="Plus" class="hero-action" @click="handlePublish">
        发布文章
      </el-button>
    </section>

    <section v-if="showQuickActions" class="home-actions">
      <div v-if="isLoggedIn" class="action-card">
        <div>
          <p class="action-kicker">创作者工具</p>
          <h3>管理我的文章</h3>
          <p class="action-subtitle">查看、编辑并持续优化你的内容。</p>
        </div>
        <el-button type="primary" plain @click="goToMyArticles">进入管理</el-button>
      </div>
      <div v-if="isAdmin" class="action-card admin-card">
        <div>
          <p class="action-kicker">管理员面板</p>
          <h3>用户管理</h3>
          <p class="action-subtitle">维护站点用户与权限，保障内容质量。</p>
        </div>
        <el-button type="danger" plain @click="goToUserManagement">管理用户</el-button>
      </div>
    </section>

    <section class="home-main">
      <div class="article-area">
        <div v-if="activeFilter.type" class="filter-banner">
          <div class="filter-text">
            当前分类：
            <strong>{{ activeFilter.label }}</strong>
          </div>
          <el-button text size="small" @click="clearFilter">清除筛选</el-button>
        </div>

        <div class="article-list">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
          />
          <el-empty v-if="articles.length === 0" description="暂无文章" />
        </div>
        <div class="pagination" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            @current-change="handlePageChange"
          />
        </div>
      </div>

      <aside class="sidebar-area">
        <Sidebar
          @category-select="handleCategoryFilter"
        />
      </aside>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import ArticleCard from '@/components/article/ArticleCard.vue'
import Sidebar from '@/components/sidebar/Sidebar.vue'
import { getArticles, getArticlesByCategory } from '@/api/article'
import { useUserStore } from '@/store/user'

const articles = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeFilter = ref({ type: null, id: null, label: '' })
const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => Boolean(userStore.userId))
const isAdmin = computed(() => userStore.role === 'ADMIN')
const showQuickActions = computed(() => isLoggedIn.value || isAdmin.value)

const applyPageResult = (res) => {
  if (!res) {
    articles.value = []
    total.value = 0
    return
  }

  if (Array.isArray(res.list)) {
    articles.value = res.list
    total.value = Number(res.total || res.list.length)
    return
  }

  if (Array.isArray(res.records)) {
    articles.value = res.records
    total.value = Number(res.total || res.records.length)
    return
  }

  if (Array.isArray(res)) {
    articles.value = res
    total.value = res.length
    return
  }

  articles.value = []
  total.value = 0
}

const loadArticles = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    let res

    if (activeFilter.value.type === 'category') {
      res = await getArticlesByCategory(activeFilter.value.id, params)
    } else {
      res = await getArticles(params)
    }

    applyPageResult(res)
  } catch (error) {
    console.error('Failed to load articles', error)
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadArticles()
}

const handleCategoryFilter = (category) => {
  if (activeFilter.value.type === 'category' && activeFilter.value.id === category.id) {
    return
  }
  activeFilter.value = {
    type: 'category',
    id: category.id,
    label: category.name
  }
  currentPage.value = 1
  loadArticles()
}

const clearFilter = () => {
  if (!activeFilter.value.type) {
    return
  }
  activeFilter.value = { type: null, id: null, label: '' }
  currentPage.value = 1
  loadArticles()
}

const handlePublish = () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录再发布文章')
    router.push('/login')
    return
  }
  router.push({ name: 'ArticleCreate' })
}

const goToMyArticles = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录再管理文章')
    router.push('/login')
    return
  }
  router.push({ name: 'MyArticles' })
}

const goToUserManagement = () => {
  if (!isAdmin.value) {
    ElMessage.warning('仅管理员可访问此功能')
    return
  }
  router.push({ name: 'AdminUsers' })
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.home-layout {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.home-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 32px;
  border: 1px solid var(--page-border-color);
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04);
}

.hero-action {
  white-space: nowrap;
}

.hero-text h1 {
  margin: 8px 0;
  font-size: 28px;
  color: #1f2d3d;
}

.hero-kicker {
  margin: 0;
  font-size: 13px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--page-muted-color);
}

.hero-subtitle {
  margin: 0;
  color: #5c6b7a;
}

.home-main {
  display: grid;
  grid-template-columns: minmax(0, 2fr) 340px;
  gap: 32px;
  align-items: flex-start;
}

.home-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.action-card {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 20px;
  border: 1px solid var(--page-border-color);
  border-radius: 12px;
  background-color: #f8fafc;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
}

.admin-card {
  background-color: #fff5f5;
  border-color: #fecaca;
}

.action-kicker {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--page-muted-color);
}

.action-subtitle {
  margin: 6px 0 0;
  color: #5c6b7a;
}

.article-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  background-color: #f8fafc;
}

.filter-text {
  font-size: 14px;
  color: #303133;
}

.sidebar-area {
  position: sticky;
  top: 96px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

@media (max-width: 1024px) {
  .home-main {
    grid-template-columns: 1fr;
  }

  .sidebar-area {
    position: static;
    width: 100%;
  }
}

@media (max-width: 640px) {
  .home-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 24px;
  }

  .hero-text h1 {
    font-size: 24px;
  }

  .action-card {
    flex-direction: column;
  }
}
</style>

<template>
  <div class="admin-users-page">
    <div class="page-header">
      <div>
        <p class="page-kicker">后台管理</p>
        <h2>用户管理</h2>
        <p class="page-subtitle">监控站点用户并进行基础维护操作。</p>
      </div>
    </div>

    <el-card shadow="never">
      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名 / 昵称 / 邮箱"
          clearable
          class="filter-input"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-select v-model="role" placeholder="角色" clearable class="filter-select">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="普通用户" value="USER" />
        </el-select>
        <el-button type="primary" plain @click="handleSearch">筛选</el-button>
      </div>

      <el-table :data="users" v-loading="loading" empty-text="暂无数据">
        <el-table-column label="用户" min-width="260">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :src="resolveAssetUrl(row.avatar)" size="40" />
              <div>
                <div class="user-name">{{ row.nickname || row.username }}</div>
                <div class="user-meta">{{ row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="140">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
              {{ formatRole(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="200">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewProfile(row)">
              查看主页
            </el-button>
            <el-button type="primary" link size="small" @click="openArticleDrawer(row)">
              管理文章
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination" v-if="total > 0">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :page-sizes="[10, 20, 50]"
          :total="total"
          :current-page="currentPage"
          :page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>

  <el-drawer
    v-model="articleDrawerVisible"
    :title="selectedUser ? `文章管理 · ${selectedUser.nickname || selectedUser.username}` : '文章管理'"
    size="50%"
    @closed="handleArticleDrawerClosed"
  >
    <div v-if="selectedUser" class="article-panel">
      <div class="article-panel-header">
        <h3>{{ selectedUser.nickname || selectedUser.username }}</h3>
        <p class="article-panel-meta">共 {{ articleTotal }} 篇文章</p>
      </div>

      <el-table :data="userArticles" v-loading="articleLoading" empty-text="暂无文章">
        <el-table-column label="标题" min-width="220">
          <template #default="{ row }">
            <span class="article-title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="140">
          <template #default="{ row }">{{ row.category?.name || '未分类' }}</template>
        </el-table-column>
        <el-table-column label="发布时间" width="200">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDeleteArticle(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="article-table-pagination" v-if="articleTotal > 0">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="articleTotal"
          :current-page="articlePageNum"
          :page-size="articlePageSize"
          @current-change="handleArticlePageChange"
        />
      </div>
    </div>
    <div v-else class="article-panel empty">请选择用户查看文章</div>
  </el-drawer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, getUserArticles } from '@/api/user'
import { deleteArticle } from '@/api/article'
import { resolveAssetUrl } from '@/utils/url'

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const keyword = ref('')
const role = ref('')
const articleDrawerVisible = ref(false)
const selectedUser = ref(null)
const userArticles = ref([])
const articleTotal = ref(0)
const articlePageNum = ref(1)
const articlePageSize = ref(10)
const articleLoading = ref(false)
const router = useRouter()

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      role: role.value || undefined
    }
    const res = await getUsers(params)
    users.value = res?.list || []
    total.value = res?.total || 0
  } catch (error) {
    console.error('Failed to load users', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchUsers()
}

const formatRole = (value) => {
  if (value === 'ADMIN') {
    return '管理员'
  }
  return '普通用户'
}

const formatDate = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

const openArticleDrawer = (user) => {
  selectedUser.value = user
  articleDrawerVisible.value = true
  articlePageNum.value = 1
  fetchUserArticles()
}

const viewProfile = (user) => {
  if (!user?.id) {
    return
  }
  router.push({ name: 'AuthorProfile', params: { id: user.id } })
}

const fetchUserArticles = async () => {
  if (!selectedUser.value) return
  articleLoading.value = true
  try {
    const res = await getUserArticles(selectedUser.value.id, {
      pageNum: articlePageNum.value,
      pageSize: articlePageSize.value
    })
    userArticles.value = res?.list || []
    articleTotal.value = res?.total || 0
  } catch (error) {
    console.error('Failed to load user articles', error)
    ElMessage.error('加载文章列表失败')
  } finally {
    articleLoading.value = false
  }
}

const handleArticlePageChange = (page) => {
  articlePageNum.value = page
  fetchUserArticles()
}

const handleDeleteArticle = async (article) => {
  if (!article?.id) {
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定删除文章《${article.title}》？此操作不可撤销。`,
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteArticle(article.id)
    ElMessage.success('文章已删除')
    fetchUserArticles()
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }
    console.error('Failed to delete article', error)
    ElMessage.error('删除文章失败')
  }
}

const handleArticleDrawerClosed = () => {
  selectedUser.value = null
  userArticles.value = []
  articleTotal.value = 0
  articlePageNum.value = 1
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-users-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header h2 {
  margin: 4px 0;
  font-size: 28px;
  color: #1f2d3d;
}

.page-kicker {
  margin: 0;
  font-size: 13px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--page-muted-color);
}

.page-subtitle {
  margin: 0;
  color: #5c6b7a;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.filter-input {
  flex: 1;
  min-width: 240px;
}

.filter-select {
  width: 140px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-weight: 600;
  color: #1f2d3d;
}

.user-meta {
  font-size: 12px;
  color: #909399;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.article-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.article-panel-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.article-panel-header h3 {
  margin: 0;
  font-size: 20px;
}

.article-panel-meta {
  margin: 0;
  color: #909399;
}

.article-title {
  font-weight: 500;
  color: #1f2d3d;
}

.article-table-pagination {
  display: flex;
  justify-content: flex-end;
}

.article-panel.empty {
  color: #909399;
  text-align: center;
  padding-top: 40px;
}
</style>

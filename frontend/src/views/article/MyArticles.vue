<template>
  <div class="my-articles-page">
    <div class="page-header">
      <div>
        <p class="page-kicker">内容管理</p>
        <h2>我的文章</h2>
        <p class="page-subtitle">查看、编辑并维护你已发布的文章。</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="goCreate">
        发布新文章
      </el-button>
    </div>

    <el-card shadow="never">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索文章标题"
          clearable
          class="toolbar-search"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button @click="handleSearch">搜索</el-button>
      </div>

      <el-table :data="articles" v-loading="loading" empty-text="暂无文章">
        <el-table-column label="标题" min-width="280">
          <template #default="{ row }">
            <div class="title-cell">
              <span class="title">{{ row.title }}</span>
              <span class="muted">{{ row.category?.name || '未分类' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="200">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row.id)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination" v-if="total > 0">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :current-page="currentPage"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { deleteArticle } from '@/api/article'
import { getUserArticles } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const articles = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const keyword = ref('')

const extractPageData = (data) => {
  if (!data) {
    return { list: [], total: 0 }
  }
  if (Array.isArray(data.list)) {
    return { list: data.list, total: Number(data.total || data.list.length) }
  }
  if (Array.isArray(data.records)) {
    return { list: data.records, total: Number(data.total || data.records.length) }
  }
  if (Array.isArray(data)) {
    return { list: data, total: data.length }
  }
  return { list: [], total: 0 }
}

const fetchArticles = async () => {
  if (!userStore.userId) {
    articles.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const searchKeyword = keyword.value.trim()
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword || undefined
    }
    const res = await getUserArticles(userStore.userId, params)
    const { list, total: totalCount } = extractPageData(res)
    articles.value = list
    total.value = totalCount
  } catch (error) {
    console.error('Failed to load my articles', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchArticles()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchArticles()
}

const goCreate = () => {
  router.push({ name: 'ArticleCreate' })
}

const handleEdit = (id) => {
  router.push({ name: 'ArticleEdit', params: { id } })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除《${row.title}》吗？`, '删除文章', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteArticle(row.id)
      ElMessage.success('删除成功')
      fetchArticles()
    } catch (error) {
      console.error('Failed to delete article', error)
    }
  }).catch(() => {})
}

const formatDate = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString()
}

watch(() => userStore.userId, fetchArticles)
onMounted(fetchArticles)
</script>

<style scoped>
.my-articles-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-kicker {
  margin: 0;
  font-size: 13px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--page-muted-color);
}

.page-header h2 {
  margin: 4px 0;
  font-size: 28px;
  color: #1f2d3d;
}

.page-subtitle {
  margin: 0;
  color: #5c6b7a;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.toolbar-search {
  flex: 1;
}

.title-cell {
  display: flex;
  flex-direction: column;
}

.title-cell .title {
  font-weight: 600;
}

.title-cell .muted {
  font-size: 12px;
  color: #909399;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>

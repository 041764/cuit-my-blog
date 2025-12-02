<template>
  <div class="my-following-page">
    <div class="page-header">
      <div>
        <p class="page-kicker">社交关系</p>
        <h2>我的关注</h2>
        <p class="page-subtitle">集中查看你已关注的作者，快速前往主页或取消关注。</p>
      </div>
    </div>

    <el-card shadow="never" v-loading="loading">
      <div v-if="!loading && followingList.length === 0" class="empty-state">
        <el-empty description="暂未关注任何人" />
      </div>

      <div v-else class="following-list">
        <div
          v-for="user in followingList"
          :key="user.id"
          class="following-item"
        >
          <div class="user-summary">
            <el-avatar :src="resolveAssetUrl(user.avatar)" size="64" />
            <div class="user-text">
              <div class="user-name">{{ user.nickname || user.username }}</div>
              <div class="user-bio">{{ user.bio || '这个用户很神秘，暂无简介。' }}</div>
              <div class="follow-meta">关注于 {{ formatDate(user.followedAt) }}</div>
            </div>
          </div>
          <div class="item-actions">
            <el-button type="primary" link @click="viewProfile(user.id)">
              查看主页
            </el-button>
            <FollowButton
              :user-id="user.id"
              @change="handleFollowStateChange(user.id, $event)"
            />
          </div>
        </div>
      </div>

      <div class="list-pagination" v-if="total > pageSize">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyFollowing } from '@/api/follow'
import FollowButton from '@/components/interaction/FollowButton.vue'
import { resolveAssetUrl } from '@/utils/url'

const router = useRouter()
const followingList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const normalizePageResult = (payload) => {
  if (!payload) {
    return { list: [], total: 0 }
  }
  if (Array.isArray(payload.list)) {
    return { list: payload.list, total: Number(payload.total || payload.list.length) }
  }
  if (Array.isArray(payload.records)) {
    return { list: payload.records, total: Number(payload.total || payload.records.length) }
  }
  if (Array.isArray(payload)) {
    return { list: payload, total: payload.length }
  }
  return { list: [], total: 0 }
}

const fetchFollowing = async () => {
  loading.value = true
  try {
    const res = await getMyFollowing({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    const { list, total: totalCount } = normalizePageResult(res)
    followingList.value = list
    total.value = totalCount
  } catch (error) {
    console.error('Failed to load following list', error)
    ElMessage.error('加载关注列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchFollowing()
}

const viewProfile = (userId) => {
  router.push({ name: 'AuthorProfile', params: { id: userId } })
}

const handleFollowStateChange = (userId, isFollowing) => {
  if (isFollowing) {
    return
  }
  if (followingList.value.length === 1 && pageNum.value > 1) {
    pageNum.value -= 1
  }
  fetchFollowing()
}

const formatDate = (value) => {
  if (!value) {
    return '-'
  }
  return new Date(value).toLocaleString()
}

onMounted(fetchFollowing)
</script>

<style scoped>
.my-following-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
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

.empty-state {
  padding: 40px 0;
}

.following-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.following-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
}

.following-item:last-child {
  border-bottom: none;
}

.user-summary {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2d3d;
}

.user-bio {
  margin: 0;
  color: #606266;
}

.follow-meta {
  font-size: 12px;
  color: #909399;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.list-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .following-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .item-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>

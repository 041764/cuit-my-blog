<template>
  <header class="site-header">
    <div class="header-inner">
      <div class="logo">
        <router-link to="/">Blog System</router-link>
      </div>
      <el-menu
        class="header-menu"
        mode="horizontal"
        :router="true"
        :default-active="$route.path"
        :ellipsis="false"
      >
        <el-menu-item index="/">首页</el-menu-item>
      </el-menu>
      <div class="right-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章..."
          class="search-input"
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <div v-if="userStore.userId" class="user-info">
          <el-dropdown>
            <span class="el-dropdown-link user-dropdown-trigger">
              <el-avatar
                class="user-avatar"
                :src="userAvatar"
                :size="32"
                shape="circle"
              />
              <span class="user-name">{{ userDisplayName }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push({ name: 'MyArticles' })">我的文章</el-dropdown-item>
                <el-dropdown-item @click="$router.push({ name: 'MyFollowing' })">我的关注</el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" @click="$router.push({ name: 'AdminUsers' })">用户管理</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div v-else class="auth-links">
          <router-link to="/login">登录</router-link>
          <span class="divider">|</span>
          <router-link to="/register">注册</router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter, useRoute } from 'vue-router'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { resolveAssetUrl } from '@/utils/url'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const searchKeyword = ref(route.query.keyword ? String(route.query.keyword) : '')
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const userDisplayName = computed(() => userStore.userInfo?.nickname || 'User')

const isAdmin = computed(() => userStore.role === 'ADMIN')

const userAvatar = computed(() => {
  const resolved = resolveAssetUrl(userStore.userInfo?.avatar)
  return resolved || DEFAULT_AVATAR
})

watch(
  () => route.query.keyword,
  (val) => {
    if (typeof val === 'string') {
      searchKeyword.value = val
    } else if (!route.query.keyword && route.name !== 'ArticleSearch') {
      searchKeyword.value = ''
    }
  }
)

const handleSearch = () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    searchKeyword.value = ''
    if (route.name === 'ArticleSearch') {
      router.replace({ name: 'Home' }).catch(() => {})
    } else if (route.name !== 'Home') {
      router.push({ name: 'Home' }).catch(() => {})
    }
    return
  }

  router.push({
    name: 'ArticleSearch',
    query: {
      keyword,
      pageNum: 1
    }
  }).catch(() => {})
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.site-header {
  width: 100%;
  background-color: #ffffff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.header-inner {
  max-width: var(--page-max-width);
  margin: 0 auto;
  height: 72px;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 var(--page-padding-x);
}

.logo a {
  font-size: 22px;
  font-weight: 700;
  color: #1f2d3d;
}

.header-menu {
  flex: 1;
  border-bottom: none;
  background-color: transparent;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 240px;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--el-color-primary);
  font-weight: 500;
}

.user-dropdown-trigger {
  gap: 8px;
}

.user-avatar {
  border: 2px solid #f3f4f6;
}

.user-name {
  line-height: 1;
}

.auth-links {
  display: flex;
  align-items: center;
  color: #606266;
}

.auth-links a {
  color: #606266;
}

.auth-links a:hover {
  color: var(--el-color-primary);
}

.auth-links .divider {
  margin: 0 8px;
  color: #dcdfe6;
}

@media (max-width: 768px) {
  .header-inner {
    height: auto;
    flex-wrap: wrap;
    padding: 12px var(--page-padding-x);
    gap: 12px;
  }

  .header-menu {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .right-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>

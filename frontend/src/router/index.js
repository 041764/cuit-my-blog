import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'search',
        name: 'ArticleSearch',
        component: () => import('@/views/article/SearchResult.vue')
      },
      {
        path: 'article/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/ArticleDetail.vue')
      },
      {
        path: 'author/:id',
        name: 'AuthorProfile',
        component: () => import('@/views/user/AuthorProfile.vue')
      },
      {
        path: 'article/editor',
        name: 'ArticleCreate',
        component: () => import('@/views/article/ArticleEditor.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'article/editor/:id',
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEditor.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'dashboard/articles',
        name: 'MyArticles',
        component: () => import('@/views/article/MyArticles.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'dashboard/following',
        name: 'MyFollowing',
        component: () => import('@/views/user/MyFollowing.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/user/AdminUsers.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.userId) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  if (to.meta.requiresAdmin && userStore.role !== 'ADMIN') {
    next({ name: 'Home' })
    return
  }
  next()
})

export default router

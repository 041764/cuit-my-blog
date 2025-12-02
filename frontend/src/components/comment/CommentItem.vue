<template>
  <div class="comment-item">
    <div class="comment-header">
      <el-avatar :size="32" :src="authorAvatar" />
      <span class="author-name">{{ authorName }}</span>
      <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
      <el-button link type="primary" size="small" @click="showReply = !showReply">回复</el-button>
      <el-button v-if="isAuthor" link type="danger" size="small" @click="handleDelete">删除</el-button>
    </div>
    <div class="comment-content">
      {{ comment.content }}
    </div>
    
    <!-- Reply Input -->
    <div v-if="showReply" class="reply-input-wrapper">
      <CommentInput 
        :article-id="articleId" 
        :parent-id="comment.id" 
        :placeholder="`回复 @${authorName}`"
        @success="handleReplySuccess"
      />
    </div>

    <!-- Children Comments -->
    <div v-if="comment.children && comment.children.length > 0" class="comment-children">
      <CommentItem 
        v-for="child in comment.children" 
        :key="child.id" 
        :comment="child" 
        :article-id="articleId"
        @refresh="emit('refresh')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { deleteComment } from '@/api/comment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { resolveAssetUrl } from '@/utils/url'
import CommentInput from './CommentInput.vue'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  articleId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['refresh'])

const userStore = useUserStore()
const showReply = ref(false)
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const authorInfo = computed(() => props.comment.user || {})

const authorName = computed(() => {
  return authorInfo.value.nickname || authorInfo.value.username || '匿名用户'
})

const authorAvatar = computed(() => {
  const resolved = resolveAssetUrl(authorInfo.value.avatar)
  return resolved || DEFAULT_AVATAR
})

const isAuthor = computed(() => {
  if (!userStore.userId || !authorInfo.value.id) {
    return false
  }
  return String(userStore.userId) === String(authorInfo.value.id)
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const handleReplySuccess = () => {
  showReply.value = false
  emit('refresh')
}

const handleDelete = () => {
  ElMessageBox.confirm(
    '确定要删除这条评论吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteComment(props.comment.id)
      ElMessage.success('删除成功')
      emit('refresh')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}
</script>

<style scoped>
.comment-item {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.author-name {
  font-weight: bold;
  font-size: 14px;
}
.comment-time {
  color: #999;
  font-size: 12px;
  flex-grow: 1;
}
.comment-content {
  margin-left: 42px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}
.reply-input-wrapper {
  margin-left: 42px;
  margin-top: 10px;
}
.comment-children {
  margin-left: 42px;
  margin-top: 15px;
  padding-left: 15px;
  border-left: 2px solid #f0f0f0;
}
</style>

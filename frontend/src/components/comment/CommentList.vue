<template>
  <div class="comment-list-container">
    <h3>评论 ({{ total }})</h3>
    
    <CommentInput :article-id="articleId" @success="fetchComments" />
    
    <div v-loading="loading" class="comment-list">
      <div v-if="comments.length === 0" class="empty-comments">
        暂无评论，快来抢沙发吧！
      </div>
      <CommentItem 
        v-for="comment in comments" 
        :key="comment.id" 
        :comment="comment" 
        :article-id="articleId"
        @refresh="fetchComments"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getCommentsByArticleId } from '@/api/comment'
import CommentInput from './CommentInput.vue'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  articleId: {
    type: [String, Number],
    required: true
  }
})

const comments = ref([])
const total = ref(0)
const loading = ref(false)

const fetchComments = async () => {
  if (!props.articleId) return
  loading.value = true
  try {
    const data = await getCommentsByArticleId(props.articleId)
    comments.value = data || []
    total.value = countComments(comments.value)
  } catch (error) {
    console.error('Failed to fetch comments', error)
  } finally {
    loading.value = false
  }
}

const countComments = (list) => {
  let count = 0
  if (!list) return 0
  for (const item of list) {
    count++
    if (item.children) {
      count += countComments(item.children)
    }
  }
  return count
}

watch(() => props.articleId, () => {
  fetchComments()
})

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-list-container {
  margin-top: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}
.empty-comments {
  text-align: center;
  color: #999;
  padding: 30px 0;
}
</style>

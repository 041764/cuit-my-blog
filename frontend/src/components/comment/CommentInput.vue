<template>
  <div class="comment-input">
    <el-input
      v-model="content"
      type="textarea"
      :rows="3"
      :placeholder="placeholder || '写下你的评论...'"
      maxlength="500"
      show-word-limit
    />
    <div class="input-actions">
      <el-button type="primary" @click="handleSubmit" :loading="submitting" size="small">
        发表评论
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { createComment } from '@/api/comment'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const props = defineProps({
  articleId: {
    type: [String, Number],
    required: true
  },
  parentId: {
    type: [String, Number],
    default: null
  },
  placeholder: String
})

const emit = defineEmits(['success'])

const content = ref('')
const submitting = ref(false)
const userStore = useUserStore()

const handleSubmit = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (!content.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  submitting.value = true
  try {
    const data = {
      articleId: props.articleId,
      content: content.value,
      parentId: props.parentId
    }
    const res = await createComment(data)
    ElMessage.success('评论成功')
    content.value = ''
    emit('success', res)
  } catch (error) {
    ElMessage.error('评论失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.comment-input {
  margin-bottom: 20px;
}
.input-actions {
  margin-top: 10px;
  text-align: right;
}
</style>

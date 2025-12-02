<template>
  <el-button 
    v-if="!isSelf"
    :type="isFollowing ? 'info' : 'primary'" 
    :loading="loading"
    @click="handleFollow"
    size="small"
    round
    plain
  >
    {{ isFollowing ? '已关注' : '关注' }}
  </el-button>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { followUser, unfollowUser, getFollowStatus } from '@/api/follow'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['change'])

const isFollowing = ref(false)
const loading = ref(false)
const userStore = useUserStore()

const isSelf = computed(() => {
  return userStore.userId && String(userStore.userId) === String(props.userId)
})

const fetchStatus = async () => {
  if (!props.userId || isSelf.value) return

  try {
    const data = await getFollowStatus(props.userId)
    isFollowing.value = data
  } catch (error) {
    console.error('Failed to fetch follow status', error)
  }
}

const handleFollow = async () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  loading.value = true
  try {
    if (isFollowing.value) {
      await unfollowUser(props.userId)
      isFollowing.value = false
      ElMessage.success('已取消关注')
    } else {
      await followUser(props.userId)
      isFollowing.value = true
      ElMessage.success('关注成功')
    }
    emit('change', isFollowing.value)
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

watch(() => props.userId, () => {
  fetchStatus()
})

onMounted(() => {
  fetchStatus()
})
</script>

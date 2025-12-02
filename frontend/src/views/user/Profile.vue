<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
        </div>
      </template>
      
      <div class="profile-content">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :http-request="handleAvatarUpload"
            :before-upload="beforeAvatarUpload"
          >
            <img
              v-if="userForm.avatar"
              :src="resolveAssetUrl(userForm.avatar)"
              class="avatar"
            />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            <div class="upload-tip">点击更换头像</div>
          </el-upload>
        </div>

        <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="80px" class="user-form">
          <el-form-item label="用户名">
            <el-input v-model="userForm.username" disabled />
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="userForm.nickname" />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" />
          </el-form-item>

          <el-form-item label="简介" prop="bio">
            <el-input v-model="userForm.bio" type="textarea" :rows="4" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm(userFormRef)">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, uploadAvatar } from '@/api/user'
import { useUserStore } from '@/store/user'
import { resolveAssetUrl } from '@/utils/url'

const userStore = useUserStore()
const userFormRef = ref(null)

const userForm = reactive({
  username: '',
  nickname: '',
  email: '',
  bio: '',
  avatar: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
}

const applyProfileData = (data) => {
  if (!data) {
    return
  }
  userForm.username = data.username || ''
  userForm.nickname = data.nickname || ''
  userForm.email = data.email || ''
  userForm.bio = data.bio || ''
  userForm.avatar = data.avatar || ''
  userStore.updateUserInfo({
    username: data.username,
    nickname: data.nickname,
    email: data.email,
    bio: data.bio,
    avatar: data.avatar
  })
}

onMounted(async () => {
  await loadUserProfile()
})

const loadUserProfile = async () => {
  try {
    const data = await getUserProfile()
    applyProfileData(data)
  } catch (error) {
    console.error('Failed to load user profile', error)
    ElMessage.error('加载个人资料失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt15M = file.size / 1024 / 1024 < 15

  if (!isJPGOrPNG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt15M) {
    ElMessage.error('上传头像图片大小不能超过 15MB!')
  }
  return isJPGOrPNG && isLt15M
}

const handleAvatarUpload = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const data = await uploadAvatar(formData)
    applyProfileData(data)
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('上传头像失败')
  }
}

const submitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        const payload = {
          nickname: userForm.nickname,
          bio: userForm.bio
        }
        const data = await updateUserProfile(payload)
        applyProfileData(data)
        ElMessage.success('保存成功')
      } catch (error) {
        ElMessage.error('保存失败')
      }
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-section {
  margin-bottom: 30px;
  text-align: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  border: 1px dashed #d9d9d9;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}

.upload-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

.user-form {
  width: 100%;
  max-width: 500px;
}
</style>

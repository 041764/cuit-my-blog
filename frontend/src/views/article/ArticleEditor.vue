<template>
  <div class="article-editor-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEditMode ? '编辑文章' : '发布文章' }}</span>
        </div>
      </template>
      
      <el-form :model="articleForm" :rules="rules" ref="articleFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-form-item label="封面" prop="coverImage">
          <el-upload
            class="cover-uploader"
            action="#"
            :show-file-list="false"
            :http-request="handleCoverUpload"
            :before-upload="beforeCoverUpload"
          >
            <img
              v-if="articleForm.coverImage"
              :src="resolveAssetUrl(articleForm.coverImage)"
              class="cover-image"
            />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="articleForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in categories"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="内容" prop="content">
          <v-md-editor 
            v-model="articleForm.content" 
            height="500px"
            :disabled-menus="[]"
            @upload-image="handleEditorUploadImage"
          ></v-md-editor>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm(articleFormRef)">
            {{ isEditMode ? '更新文章' : '发布文章' }}
          </el-button>
          <el-button @click="resetForm(articleFormRef)">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategories } from '@/api/category'
import { createArticle, updateArticle, getArticleById } from '@/api/article'
import { uploadFile } from '@/api/file'
import { resolveAssetUrl } from '@/utils/url'

const route = useRoute()
const router = useRouter()
const articleFormRef = ref(null)

const isEditMode = computed(() => !!route.params.id)

const articleForm = reactive({
  title: '',
  content: '',
  coverImage: '',
  categoryId: ''
})

const categories = ref([])

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
}

onMounted(async () => {
  await loadCategories()
  if (isEditMode.value) {
    await loadArticle(route.params.id)
  }
})

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res || []
  } catch (error) {
    console.error('Failed to load categories', error)
  }
}

const loadArticle = async (id) => {
  try {
    const data = await getArticleById(id)
    articleForm.title = data.title
    articleForm.content = data.content
    articleForm.coverImage = data.coverImage
    articleForm.categoryId = data.categoryId
  } catch (error) {
    console.error('Failed to load article', error)
    ElMessage.error('加载文章失败')
  }
}

const beforeCoverUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt15M = file.size / 1024 / 1024 < 15

  if (!isJPGOrPNG) {
    ElMessage.error('上传封面图片只能是 JPG/PNG 格式!')
  }
  if (!isLt15M) {
    ElMessage.error('上传封面图片大小不能超过 15MB!')
  }
  return isJPGOrPNG && isLt15M
}

const handleCoverUpload = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const res = await uploadFile(formData, 'cover')
    articleForm.coverImage = res.url
  } catch (error) {
    ElMessage.error('上传封面失败')
  }
}

const handleEditorUploadImage = async (event, insertImage, files) => {
  try {
    const formData = new FormData()
    formData.append('file', files[0])
    const res = await uploadFile(formData, 'content')
    insertImage({
      url: resolveAssetUrl(res.url),
      desc: 'image',
    })
  } catch (error) {
    ElMessage.error('上传图片失败')
  }
}

const submitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        const data = { ...articleForm }
        
        if (isEditMode.value) {
          await updateArticle(route.params.id, data)
          ElMessage.success('更新成功')
        } else {
          await createArticle(data)
          ElMessage.success('发布成功')
        }
        router.push('/') // Or redirect to article detail
      } catch (error) {
        ElMessage.error(isEditMode.value ? '更新失败' : '发布失败')
      }
    } else {
      console.log('error submit!', fields)
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}
</script>

<style scoped>
.article-editor-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.cover-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.cover-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px; /* Center vertically */
  display: flex;
  justify-content: center;
  align-items: center;
}

.cover-image {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
</style>

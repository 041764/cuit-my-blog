<template>
  <div class="sidebar">
    <el-card class="box-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>分类</span>
        </div>
      </template>
      <ul class="category-list">
        <li v-for="category in categories" :key="category.id" class="category-item">
          <button type="button" class="category-button" @click="handleCategoryClick(category)">
            {{ category.name }}
          </button>
          <span class="count">({{ category.articleCount || 0 }})</span>
        </li>
      </ul>
    </el-card>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories } from '@/api/category'

const categories = ref([])
const emit = defineEmits(['category-select'])

onMounted(async () => {
  try {
    const categoryData = await getCategories()
    categories.value = categoryData
  } catch (error) {
    console.error('Failed to load sidebar data', error)
  }
})

const handleCategoryClick = (category) => {
  emit('category-select', category)
}
</script>

<style scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f2f5;
}

.category-item:last-child {
  border-bottom: none;
}

.category-button {
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  text-align: left;
  font: inherit;
  color: #606266;
  cursor: pointer;
  width: 100%;
}

.category-button:hover {
  color: var(--el-color-primary);
}

.category-item .count {
  color: #909399;
}

</style>

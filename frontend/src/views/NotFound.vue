<template>
  <section class="not-found">
    <div class="not-found__content">
      <p class="not-found__code">404</p>
      <h1>抱歉，页面不见了</h1>
      <p class="not-found__hint">您访问的页面不存在，或者已经被移除。</p>
      <div class="not-found__actions">
        <RouterLink class="btn" :to="{ name: 'Home' }">返回首页</RouterLink>
      </div>
    </div>
  </section>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 2rem;
}

.not-found__content {
  text-align: center;
  max-width: 420px;
}

.not-found__code {
  font-size: 5rem;
  font-weight: 700;
  color: #ff7875;
  margin: 0 0 0.5rem;
}

.not-found__hint {
  color: #666;
  margin-bottom: 1.5rem;
}

.not-found__actions .btn {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  padding: 0.6rem 1.5rem;
  border-radius: 999px;
  background: #409eff;
  color: #fff;
  font-weight: 600;
  transition: background 0.2s ease;
}

.not-found__actions .btn:hover {
  background: #347dd3;
}
</style>

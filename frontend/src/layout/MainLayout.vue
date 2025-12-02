<template>
  <div class="main-layout">
    <Header />
    <main class="main-content">
      <div class="content-container">
        <router-view />
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/layout/Header.vue'
import Footer from '@/components/layout/Footer.vue'
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #ffffff;
}

.main-content {
  flex: 1;
  width: 100%;
  background-color: #ffffff;
}

.content-container {
  width: 100%;
  max-width: var(--page-max-width);
  margin: 0 auto;
  padding: 32px var(--page-padding-x) 48px;
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .content-container {
    padding: 24px var(--page-padding-x) 32px;
  }
}
</style>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/user'
import NavigationBar from './components/NavigationBar.vue'
import FooterComponent from './components/FooterComponent.vue'

const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {
  // 全局初始化：加載用戶資料
  try {
    await userStore.fetchProfile()
  } catch (error) {
    console.error('Failed to load user profile:', error)
  }
})
</script>

<template>
  <div id="app" class="min-h-screen flex flex-col bg-gray-50">
    <!-- 导航栏 -->
    <NavigationBar />
    
    <!-- 主要内容区域 -->
    <main class="flex-1">
      <RouterView />
    </main>
    
    <!-- 页脚 -->
    <FooterComponent />
  </div>
</template>

<style>
/* 全局样式 */
html, body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
}

* {
  box-sizing: border-box;
}

/* 响应式字体大小 */
html {
  font-size: 16px;
}

@media (max-width: 768px) {
  html {
    font-size: 14px;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 基础动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Tailwind CSS 基础样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

@media (min-width: 640px) {
  .container {
    padding: 0 2rem;
  }
}
</style>

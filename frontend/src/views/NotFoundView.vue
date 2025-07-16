<script setup lang="ts">
import { ArrowLeft, Home } from 'lucide-vue-next'
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="text-center px-4">
      <!-- 404 大数字 -->
      <div class="mb-8">
        <h1 class="text-9xl font-bold text-gray-300 select-none">404</h1>
      </div>
      
      <!-- 错误信息 -->
      <div class="mb-8">
        <h2 class="text-3xl font-bold text-gray-800 mb-4">页面未找到</h2>
        <p class="text-lg text-gray-600 max-w-md mx-auto">
          抱歉，您访问的页面不存在或已被移动。请检查网址是否正确，或返回首页继续浏览。
        </p>
      </div>
      
      <!-- 操作按钮 -->
      <div class="space-y-4 sm:space-y-0 sm:space-x-4 sm:flex sm:justify-center">
        <button
          @click="$router.go(-1)"
          class="inline-flex items-center px-6 py-3 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors duration-200 font-medium"
        >
          <ArrowLeft class="w-4 h-4 mr-2" />
          返回上页
        </button>
        
        <router-link
          to="/"
          class="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 font-medium"
        >
          <Home class="w-4 h-4 mr-2" />
          回到首页
        </router-link>
      </div>
      
      <!-- 建议链接 -->
      <div class="mt-12">
        <p class="text-gray-500 mb-4">您可能在寻找：</p>
        <div class="flex flex-wrap justify-center gap-4">
          <router-link
            to="/about"
            class="text-blue-600 hover:text-blue-700 underline"
          >
            关于我
          </router-link>
          <router-link
            to="/blog"
            class="text-blue-600 hover:text-blue-700 underline"
          >
            博客文章
          </router-link>
          <router-link
            to="/media"
            class="text-blue-600 hover:text-blue-700 underline"
          >
            媒体库
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 404 数字的渐变效果 */
h1 {
  background: linear-gradient(45deg, #e5e7eb, #d1d5db);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 添加一些动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.text-center > div {
  animation: fadeInUp 0.6s ease-out;
}

.text-center > div:nth-child(2) {
  animation-delay: 0.2s;
}

.text-center > div:nth-child(3) {
  animation-delay: 0.4s;
}

.text-center > div:nth-child(4) {
  animation-delay: 0.6s;
}
</style> 
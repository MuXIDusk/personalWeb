<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Menu, X, Home, User, BookOpen, MessageSquare, FileText } from 'lucide-vue-next'

const route = useRoute()
const isMenuOpen = ref(false)

const navigationItems = [
  { name: '首页', path: '/', icon: Home },
  { name: '关于我', path: '/about', icon: User },
  { name: '媒体库', path: '/media', icon: BookOpen },
  { name: '政治观点', path: '/political', icon: MessageSquare },
  { name: '博客', path: '/blog', icon: FileText }
]

const isActiveRoute = (path: string) => {
  return route.path === path || (path !== '/' && route.path.startsWith(path))
}

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const closeMenu = () => {
  isMenuOpen.value = false
}
</script>

<template>
  <nav class="bg-white shadow-lg sticky top-0 z-50">
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center h-16">
        <!-- Logo -->
        <router-link to="/" class="flex items-center space-x-2 text-xl font-bold text-gray-800 hover:text-blue-600 transition-colors">
          <img src="/src/assets/logo.svg" alt="Logo" class="h-8 w-8" />
          <span>个人网站</span>
        </router-link>

        <!-- Desktop Navigation -->
        <div class="hidden md:flex items-center space-x-8">
          <router-link
            v-for="item in navigationItems"
            :key="item.path"
            :to="item.path"
            class="flex items-center space-x-1 px-3 py-2 rounded-lg text-gray-700 hover:text-blue-600 hover:bg-blue-50 transition-all duration-200"
            :class="{
              'text-blue-600 bg-blue-50 font-medium': isActiveRoute(item.path)
            }"
          >
            <component :is="item.icon" class="w-4 h-4" />
            <span>{{ item.name }}</span>
          </router-link>
        </div>

        <!-- Mobile menu button -->
        <button
          @click="toggleMenu"
          class="md:hidden flex items-center justify-center w-10 h-10 rounded-lg text-gray-700 hover:bg-gray-100 transition-colors"
        >
          <Menu v-if="!isMenuOpen" class="w-6 h-6" />
          <X v-else class="w-6 h-6" />
        </button>
      </div>

      <!-- Mobile Navigation -->
      <div
        v-show="isMenuOpen"
        class="md:hidden border-t border-gray-200 py-4 space-y-2"
      >
        <router-link
          v-for="item in navigationItems"
          :key="item.path"
          :to="item.path"
          @click="closeMenu"
          class="flex items-center space-x-3 px-4 py-3 rounded-lg text-gray-700 hover:text-blue-600 hover:bg-blue-50 transition-all duration-200"
          :class="{
            'text-blue-600 bg-blue-50 font-medium': isActiveRoute(item.path)
          }"
        >
          <component :is="item.icon" class="w-5 h-5" />
          <span>{{ item.name }}</span>
        </router-link>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* 确保在移动设备上的过渡效果 */
.router-link-active {
  @apply text-blue-600 bg-blue-50 font-medium;
}

/* 移动端菜单动画 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style> 
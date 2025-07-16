<script setup lang="ts">
import { Github, Twitter, Mail, Heart } from 'lucide-vue-next'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

const currentYear = new Date().getFullYear()

const quickLinks = [
  { name: '首页', path: '/' },
  { name: '关于我', path: '/about' },
  { name: '媒体库', path: '/media' },
  { name: '博客', path: '/blog' }
]

const socialLinks = [
  { name: 'GitHub', icon: Github, url: 'https://github.com' },
  { name: 'Twitter', icon: Twitter, url: 'https://twitter.com' },
  { name: 'Email', icon: Mail, url: 'mailto:contact@example.com' }
]
</script>

<template>
  <footer class="bg-gray-800 text-white">
    <div class="container mx-auto px-4 py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- 个人信息 -->
        <div class="md:col-span-2">
          <h3 class="text-xl font-bold mb-4">{{ userStore.profile.name }}</h3>
          <p class="text-gray-300 mb-4 max-w-md">
            {{ userStore.profile.bio }}
          </p>
          <div class="flex space-x-4">
            <a
              v-for="social in socialLinks"
              :key="social.name"
              :href="social.url"
              target="_blank"
              rel="noopener noreferrer"
              class="flex items-center justify-center w-10 h-10 bg-gray-700 hover:bg-gray-600 rounded-full transition-colors duration-200"
              :title="social.name"
            >
              <component :is="social.icon" class="w-5 h-5" />
            </a>
          </div>
        </div>

        <!-- 快速链接 -->
        <div>
          <h4 class="text-lg font-semibold mb-4">快速链接</h4>
          <ul class="space-y-2">
            <li v-for="link in quickLinks" :key="link.path">
              <router-link
                :to="link.path"
                class="text-gray-300 hover:text-white transition-colors duration-200"
              >
                {{ link.name }}
              </router-link>
            </li>
          </ul>
        </div>

        <!-- 联系方式 -->
        <div>
          <h4 class="text-lg font-semibold mb-4">联系方式</h4>
          <div class="space-y-2 text-gray-300">
            <p v-if="userStore.profile.email">
              <Mail class="w-4 h-4 inline mr-2" />
              {{ userStore.profile.email }}
            </p>
            <p v-if="userStore.profile.location">
              📍 {{ userStore.profile.location }}
            </p>
          </div>
        </div>
      </div>

      <!-- 分隔线 -->
      <div class="border-t border-gray-700 mt-8 pt-8">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <p class="text-gray-400 text-sm mb-4 md:mb-0">
            © {{ currentYear }} {{ userStore.profile.name }}. 保留所有权利。
          </p>
          <p class="text-gray-400 text-sm flex items-center">
            Made with 
            <Heart class="w-4 h-4 mx-1 text-red-500" fill="currentColor" />
            using Vue.js
          </p>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
/* 确保链接在悬停时有适当的视觉反馈 */
a {
  transition: all 0.2s ease-in-out;
}

a:hover {
  text-decoration: none;
}
</style> 